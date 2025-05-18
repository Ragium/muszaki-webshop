package com.example.muszaki_shop.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.muszaki_shop.R;
import com.example.muszaki_shop.models.Review;
import com.example.muszaki_shop.notifications.ReviewNotificationHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class ProductReviewFragment extends Fragment {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    private static final int CAMERA_REQUEST_CODE = 1003;
    private static final String TAG = "ProductReviewFragment";

    private ImageView reviewImageView;
    private EditText reviewTextEdit;
    private RatingBar ratingBar;
    private Button takePhotoButton;
    private Button submitReviewButton;
    private String productId;
    private String productName;
    private Bitmap photoBitmap;
    private ReviewNotificationHelper notificationHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationHelper = new ReviewNotificationHelper(requireContext());

        reviewImageView = view.findViewById(R.id.reviewImageView);
        reviewTextEdit = view.findViewById(R.id.reviewTextEdit);
        ratingBar = view.findViewById(R.id.ratingBar);
        takePhotoButton = view.findViewById(R.id.takePhotoButton);
        submitReviewButton = view.findViewById(R.id.submitReviewButton);

        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            productName = getArguments().getString("productName");
            Log.d(TAG, "Received productId: " + productId);
        }

        takePhotoButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        submitReviewButton.setOnClickListener(v -> submitReview());
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            photoBitmap = (Bitmap) data.getExtras().get("data");
            reviewImageView.setImageBitmap(photoBitmap);
        }
    }

    private void submitReview() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Kérjük, jelentkezzen be az értékelés írásához",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String comment = reviewTextEdit.getText().toString();
        float rating = ratingBar.getRating();

        if (comment.isEmpty()) {
            Toast.makeText(requireContext(), "Kérjük, írjon értékelést",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (photoBitmap != null) {
            uploadPhotoAndReview(comment, rating);
        } else {
            saveReview(comment, rating, null);
        }
    }

    private void uploadPhotoAndReview(String comment, float rating) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();

        String photoId = UUID.randomUUID().toString();
        StorageReference photoRef = FirebaseStorage.getInstance()
                .getReference()
                .child("reviewImages")
                .child(photoId + ".jpg");

        photoRef.putBytes(photoData)
                .addOnSuccessListener(taskSnapshot -> photoRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveReview(comment, rating, uri.toString())))
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Hiba a kép feltöltése közben",
                            Toast.LENGTH_SHORT).show();
                    saveReview(comment, rating, null);
                });
    }

    private void saveReview(String comment, float rating, @Nullable String photoUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String reviewId = UUID.randomUUID().toString();

        Review review = new Review();
        review.setId(reviewId);
        review.setUid(userId);
        review.setProductId(productId);
        review.setEmail(userEmail);
        review.setRating((int) rating);
        review.setComment(comment);
        review.setImageUrl(photoUrl);
        review.setCreatedAt((int) (System.currentTimeMillis() / 1000));

        Log.d(TAG, "Saving review with productId: " + productId);

        FirebaseFirestore.getInstance()
                .collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        FirebaseFirestore.getInstance()
                                .collection("products")
                                .document(productId)
                                .collection("reviews")
                                .document(reviewId)
                                .set(review)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Értékelés sikeresen mentve",
                                            Toast.LENGTH_SHORT).show();
                                    notificationHelper.showReviewSubmittedNotification(productName);
                                    requireActivity().onBackPressed();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Hiba az értékelés mentése közben", e);
                                    Toast.makeText(requireContext(),
                                            "Hiba az értékelés mentése közben: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Log.e(TAG, "A termék nem található: " + productId);
                        Toast.makeText(requireContext(),
                                "Hiba: A termék nem található",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Hiba a termék ellenőrzése közben", e);
                    Toast.makeText(requireContext(),
                            "Hiba a termék ellenőrzése közben: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Kamera engedélyezése szükséges a kép készítéséhez",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
} 