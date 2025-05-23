// Generated by view binder compiler. Do not edit!
package com.example.muszaki_shop.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.muszaki_shop.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityRegisterBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final TextInputEditText addressInput;

  @NonNull
  public final MaterialButton backToLoginButton;

  @NonNull
  public final TextInputEditText confirmPasswordInput;

  @NonNull
  public final TextInputEditText emailInput;

  @NonNull
  public final ProgressBar loadingProgressBar;

  @NonNull
  public final TextInputEditText nameInput;

  @NonNull
  public final TextInputEditText passwordInput;

  @NonNull
  public final TextInputEditText phoneInput;

  @NonNull
  public final MaterialButton registerButton;

  private ActivityRegisterBinding(@NonNull ScrollView rootView,
      @NonNull TextInputEditText addressInput, @NonNull MaterialButton backToLoginButton,
      @NonNull TextInputEditText confirmPasswordInput, @NonNull TextInputEditText emailInput,
      @NonNull ProgressBar loadingProgressBar, @NonNull TextInputEditText nameInput,
      @NonNull TextInputEditText passwordInput, @NonNull TextInputEditText phoneInput,
      @NonNull MaterialButton registerButton) {
    this.rootView = rootView;
    this.addressInput = addressInput;
    this.backToLoginButton = backToLoginButton;
    this.confirmPasswordInput = confirmPasswordInput;
    this.emailInput = emailInput;
    this.loadingProgressBar = loadingProgressBar;
    this.nameInput = nameInput;
    this.passwordInput = passwordInput;
    this.phoneInput = phoneInput;
    this.registerButton = registerButton;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_register, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRegisterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.addressInput;
      TextInputEditText addressInput = ViewBindings.findChildViewById(rootView, id);
      if (addressInput == null) {
        break missingId;
      }

      id = R.id.backToLoginButton;
      MaterialButton backToLoginButton = ViewBindings.findChildViewById(rootView, id);
      if (backToLoginButton == null) {
        break missingId;
      }

      id = R.id.confirmPasswordInput;
      TextInputEditText confirmPasswordInput = ViewBindings.findChildViewById(rootView, id);
      if (confirmPasswordInput == null) {
        break missingId;
      }

      id = R.id.emailInput;
      TextInputEditText emailInput = ViewBindings.findChildViewById(rootView, id);
      if (emailInput == null) {
        break missingId;
      }

      id = R.id.loadingProgressBar;
      ProgressBar loadingProgressBar = ViewBindings.findChildViewById(rootView, id);
      if (loadingProgressBar == null) {
        break missingId;
      }

      id = R.id.nameInput;
      TextInputEditText nameInput = ViewBindings.findChildViewById(rootView, id);
      if (nameInput == null) {
        break missingId;
      }

      id = R.id.passwordInput;
      TextInputEditText passwordInput = ViewBindings.findChildViewById(rootView, id);
      if (passwordInput == null) {
        break missingId;
      }

      id = R.id.phoneInput;
      TextInputEditText phoneInput = ViewBindings.findChildViewById(rootView, id);
      if (phoneInput == null) {
        break missingId;
      }

      id = R.id.registerButton;
      MaterialButton registerButton = ViewBindings.findChildViewById(rootView, id);
      if (registerButton == null) {
        break missingId;
      }

      return new ActivityRegisterBinding((ScrollView) rootView, addressInput, backToLoginButton,
          confirmPasswordInput, emailInput, loadingProgressBar, nameInput, passwordInput,
          phoneInput, registerButton);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
