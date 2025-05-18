package com.example.muszaki_shop.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.muszaki_shop.adapters.ProductAdapter;
import com.example.muszaki_shop.fragments.CartFragment;
import com.example.muszaki_shop.fragments.HomeFragment;
import com.example.muszaki_shop.fragments.ProfileFragment;
import com.example.muszaki_shop.R;
import com.example.muszaki_shop.fragments.SearchFragment;
import com.example.muszaki_shop.fragments.OrdersFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_STACK_SIZE = 5;
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ProductAdapter productAdapter;
    private Stack<Fragment> fragmentStack = new Stack<>();
    private Stack<String> titleStack = new Stack<>();

    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    
    //Fragment példányok
    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private CartFragment cartFragment;
    private ProfileFragment profileFragment;
    private OrdersFragment ordersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fragmentek inicializálása
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        cartFragment = new CartFragment();
        profileFragment = new ProfileFragment();
        ordersFragment = new OrdersFragment();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.topAppBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragmentManager = getSupportFragmentManager();

        // Kijelentkezés gomb csak bejelentkezett felhasználónak
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        navigationView.getMenu().findItem(R.id.drawer_logout).setVisible(user != null);
        navigationView.getMenu().findItem(R.id.drawer_login).setVisible(user == null);
        navigationView.getMenu().findItem(R.id.drawer_register).setVisible(user == null);

        // Ha nincs savedInstanceState, akkor betöltjük a HomeFragment-et
        if (savedInstanceState == null) {
            currentFragment = homeFragment;
            setFragment(homeFragment, "Főoldal");
            bottomNavigationView.setSelectedItemId(R.id.drawer_home);
        } else {
            // Ha van savedInstanceState, visszaállítjuk az utolsó fragment-et
            currentFragment = fragmentManager.getFragment(savedInstanceState, "currentFragment");
            if (currentFragment != null) {
                setFragmentWithoutStack(currentFragment, savedInstanceState.getString("currentTitle", "Főoldal"));
                // Visszaállítjuk a bottom navigation-t
                if (currentFragment instanceof HomeFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.drawer_home);
                } else if (currentFragment instanceof SearchFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.drawer_search);
                } else if (currentFragment instanceof CartFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.drawer_cart);
                } else {
                    bottomNavigationView.setSelectedItemId(0);
                }
            } else {
                currentFragment = homeFragment;
                setFragment(homeFragment, "Főoldal");
                bottomNavigationView.setSelectedItemId(R.id.drawer_home);
            }
        }

        toolbar.setNavigationOnClickListener(v -> {
            if (!fragmentStack.isEmpty()) {
                fragmentStack.pop(); // Eltávolítjuk az aktuális fragmentet
                titleStack.pop(); // Eltávolítjuk az aktuális címet
                
                if (!fragmentStack.isEmpty()) {
                    // Visszaállítjuk az előző fragmentet és címet
                    Fragment previousFragment = fragmentStack.peek();
                    String previousTitle = titleStack.peek();
                    setFragmentWithoutStack(previousFragment, previousTitle);
                    
                    // Visszaállítjuk a bottom navigation-t
                    if (previousFragment instanceof HomeFragment) {
                        bottomNavigationView.setSelectedItemId(R.id.drawer_home);
                    } else if (previousFragment instanceof SearchFragment) {
                        bottomNavigationView.setSelectedItemId(R.id.drawer_search);
                    } else if (previousFragment instanceof CartFragment) {
                        bottomNavigationView.setSelectedItemId(R.id.drawer_cart);
                    } else {
                        bottomNavigationView.setSelectedItemId(0);
                    }
                } else {
                    // Ha nincs több fragment, akkor vissza a főoldalra
                    setFragment(homeFragment, "Főoldal");
                }
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_menu) {
                drawerLayout.openDrawer(GravityCompat.END);
                return true;
            } else if (item.getItemId() == R.id.action_profile) {
                // Profil ikonra kattintás
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this, "Bejelentkezés szükséges!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    setFragment(profileFragment, "Profil");
                    bottomNavigationView.setSelectedItemId(0); // egyik se legyen aktív
                }
            }
            return false;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.drawer_home) {
                setFragment(homeFragment, "Főoldal");
                bottomNavigationView.setSelectedItemId(R.id.drawer_home);
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (id == R.id.drawer_search) {
                setFragment(searchFragment, "Keresés");
                bottomNavigationView.setSelectedItemId(R.id.drawer_search);
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (id == R.id.drawer_cart) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this, "Bejelentkezés szükséges!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    setFragment(cartFragment, "Kosár");
                    bottomNavigationView.setSelectedItemId(R.id.drawer_cart);
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (id == R.id.drawer_orders) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this, "Bejelentkezés szükséges!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    setFragment(ordersFragment, "Rendelések");
                    bottomNavigationView.setSelectedItemId(R.id.nav_orders);
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (id == R.id.drawer_profile) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this, "Bejelentkezés szükséges!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    setFragment(profileFragment, "Profil");
                    bottomNavigationView.setSelectedItemId(0);
                }
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (id == R.id.drawer_login) {
                startActivity(new Intent(this, LoginActivity.class));
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (id == R.id.drawer_register) {
                startActivity(new Intent(this, RegisterActivity.class));
                drawerLayout.closeDrawer(GravityCompat.END);
                return true;
            } else if (id == R.id.drawer_logout) {
                FirebaseAuth.getInstance().signOut();
                navigationView.getMenu().findItem(R.id.drawer_logout).setVisible(false);
                navigationView.getMenu().findItem(R.id.drawer_login).setVisible(true);
                navigationView.getMenu().findItem(R.id.drawer_register).setVisible(true);
                setFragment(homeFragment, "Főoldal");
                bottomNavigationView.setSelectedItemId(R.id.drawer_home);
                drawerLayout.closeDrawer(GravityCompat.END);
            }
            return false;
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                setFragment(homeFragment, "Főoldal");
                return true;
            } else if (id == R.id.nav_search) {
                setFragment(searchFragment, "Keresés");
                return true;
            } else if (id == R.id.nav_cart) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this, "Bejelentkezés szükséges!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    setFragment(cartFragment, "Kosár");
                }
                return true;
            } else if (id == R.id.nav_orders) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(this, "Bejelentkezés szükséges!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    setFragment(ordersFragment, "Rendelések");
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentFragment != null) {
            fragmentManager.putFragment(outState, "currentFragment", currentFragment);
            outState.putString("currentTitle", toolbar.getTitle().toString());
        }
    }

    private void setFragment(Fragment fragment, String title) {
        if (fragmentStack.isEmpty() || fragmentStack.peek() != fragment) {
            if (fragmentStack.size() >= MAX_STACK_SIZE) {
                fragmentStack.remove(0);
                titleStack.remove(0);
            }
            fragmentStack.push(fragment);
            titleStack.push(title);
            setFragmentWithoutStack(fragment, title);
        }
    }

    private void setFragmentWithoutStack(Fragment fragment, String title) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.contentFrame, fragment);
        ft.commit();
        toolbar.setTitle(title);
        currentFragment = fragment;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else if (!fragmentStack.isEmpty()) {
            fragmentStack.pop(); // Eltávolítjuk az aktuális fragmentet
            titleStack.pop(); // Eltávolítjuk az aktuális címet
            
            if (!fragmentStack.isEmpty()) {
                // Visszaállítjuk az előző fragmentet és címet
                Fragment previousFragment = fragmentStack.peek();
                String previousTitle = titleStack.peek();
                setFragmentWithoutStack(previousFragment, previousTitle);
                
                // Visszaállítjuk a bottom navigation-t
                if (previousFragment instanceof HomeFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.drawer_home);
                } else if (previousFragment instanceof SearchFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.drawer_search);
                } else if (previousFragment instanceof CartFragment) {
                    bottomNavigationView.setSelectedItemId(R.id.drawer_cart);
                } else {
                    bottomNavigationView.setSelectedItemId(0);
                }
            } else {
                // Ha nincs több fragment, akkor vissza a főoldalra
                setFragment(homeFragment, "Főoldal");
                bottomNavigationView.setSelectedItemId(R.id.drawer_home);
            }
        } else {
            super.onBackPressed();
        }
    }

//    private void setupProductsList() {
//        List<Product> products = new ArrayList<>();
//        products.add(new Product(1, "Bosch GSR 18V-EC", "Szerszám", "Akkumulátoros csavarbehajtó, kompakt kivitel", 45000, "https://example.com/bosch_gsr.jpg"));
//        products.add(new Product(2, "Makita DHP481Z", "Szerszám", "Akkumulátoros kalapácsfúró, nagy teljesítmény", 55000, "https://example.com/makita_dhp.jpg"));
//        products.add(new Product(3, "DeWalt DCD777C2", "Szerszám", "Akkumulátoros csavarbehajtó, könnyű súly", 48000, "https://example.com/dewalt_dcd.jpg"));
//        products.add(new Product(4, "Apple MacBook Air M2", "Számítástechnika", "13'' Retina kijelzős laptop, 8GB RAM, 256GB SSD", 499000, "https://example.com/macbook_air.jpg"));
//        products.add(new Product(5, "Samsung Galaxy S23", "Mobil", "6.1'' AMOLED kijelzős okostelefon, 128GB tárhely", 299000, "https://example.com/galaxy_s23.jpg"));
//        products.add(new Product(6, "Sony WH-1000XM5", "Audio", "Vezeték nélküli zajszűrős fejhallgató", 129000, "https://example.com/sony_wh1000xm5.jpg"));
//        products.add(new Product(7, "LG OLED55C31LA", "TV & Video", "55'' 4K OLED Smart TV", 399000, "https://example.com/lg_oled.jpg"));
//        products.add(new Product(8, "Canon PIXMA TS5350a", "Irodatechnika", "Tintasugaras multifunkciós nyomtató, Wi-Fi", 39000, "https://example.com/canon_pixma.jpg"));
//
//        androidx.recyclerview.widget.RecyclerView recyclerView = findViewById(R.id.productsRecyclerView);
//        productAdapter = new ProductAdapter(products);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(productAdapter);
//    }
}