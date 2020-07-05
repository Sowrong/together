package de.sowrong.together;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import de.sowrong.together.data.Users;
import de.sowrong.together.ui.calendar.NewEditCalenderEntryActivity;
import de.sowrong.together.ui.cleaning.CleaningFragment;
import de.sowrong.together.ui.shoppingList.NewEditShoppingListEntryActivity;
import de.sowrong.together.ui.wallet.NewEditTransactionActivity;


public class MainActivity extends AppCompatActivity {
    public static final int TAB_CLEANING = 0;
    public static final int TAB_CALENDAR = 1;
    public static final int TAB_WALLET = 2;
    public static final int TAB_SHOPPING_LIST = 3;

    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;
    private Context context;

    TabLayout tabLayout;
    NavigationView navigationView;
    ViewPager2 viewPager;
    FloatingActionButton floatingActionButton;
    int previousTabPosition;

    public static final String CALENDAR_ENTRY_ID = "de.sowrong.together.CALENDAR_ENTRY_ID";
    public static final String TRANSACTION_ENTRY_ID = "de.sowrong.together.TRANSACTION_ENTRY_ID";
    public static final String SHOPPING_LIST_ENTRY_ID = "de.sowrong.together.SHOPPING_LIST_ENTRY_ID";
    public static final String SHOPPING_LIST_ITEM_ID = "de.sowrong.together.SHOPPING_LIST_ITEM_ID";
    public static final String GOTO_TAB = "de.sowrong.together.GOTO_TAB";
    public static final int TAB_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);

                switch (item.getItemId()) {
                    case R.id.nav_cleaning:
                        tabLayout.selectTab(tabLayout.getTabAt(0));
                        break;
                    case R.id.nav_calendar:
                        tabLayout.selectTab(tabLayout.getTabAt(1));
                        break;
                    case R.id.nav_wallet:
                        tabLayout.selectTab(tabLayout.getTabAt(2));
                        break;
                    case R.id.nav_shopping_list:
                        tabLayout.selectTab(tabLayout.getTabAt(3));
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);


                item.setChecked(false);

                return false;
            }
        });

        previousTabPosition = TAB_CLEANING;

        floatingActionButton = findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;

                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:
                        CleaningFragment cleaningFragment = CleaningFragment.getInstance();
                        if (cleaningFragment != null) {
                            cleaningFragment.fabClickListener();
                        }
                        break;
                    case 1:
                        intent = new Intent(context, NewEditCalenderEntryActivity.class);
                        String calendarEntryId = "";
                        intent.putExtra(CALENDAR_ENTRY_ID, calendarEntryId);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(context, NewEditTransactionActivity.class);
                        String transactionId = "";
                        intent.putExtra(TRANSACTION_ENTRY_ID, transactionId);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(context, NewEditShoppingListEntryActivity.class);
                        String shoppingListEntryId = "";
                        intent.putExtra(SHOPPING_LIST_ENTRY_ID, shoppingListEntryId);
                        startActivity(intent);
                        break;
                }
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                 */
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        new AppBarConfiguration.Builder(
                R.id.nav_cleaning, R.id.nav_calendar, R.id.nav_wallet, R.id.nav_shopping_list)
                .setDrawerLayout(drawer)
                .build();

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);

        viewPager.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch(position) {
                            case TAB_CLEANING:
                                tab.setText(R.string.tab_cleaning);
                                tab.setIcon(R.drawable.ic_tab_cleaning);
                                tab.getIcon().setAlpha(255);
                                break;
                            case TAB_CALENDAR:
                                tab.setText(R.string.tab_calendar);
                                tab.setIcon(R.drawable.ic_tab_calendar);
                                tab.getIcon().setAlpha(128);
                                break;
                            case TAB_WALLET:
                                tab.setText(R.string.tab_wallet);
                                tab.setIcon(R.drawable.ic_tab_wallet);
                                tab.getIcon().setAlpha(128);
                                break;
                            case TAB_SHOPPING_LIST:
                                tab.setText(R.string.tab_shopping_list);
                                tab.setIcon(R.drawable.ic_tab_shopping_list);
                                tab.getIcon().setAlpha(128);
                                break;
                        }
                    }
                }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int newTabPosition = tab.getPosition();
                if (previousTabPosition == TAB_CLEANING || newTabPosition == TAB_CLEANING) {
                    animateFloatingActionButton(newTabPosition);
                }
                tab.getIcon().setAlpha(255);
                viewPager.setCurrentItem(newTabPosition);
                previousTabPosition = newTabPosition;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setAlpha(128);

                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setFloatingActionButtonStyleByTabId(tabLayout.getSelectedTabPosition());

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            // User is signed in
            Log.d("Firebase", "User: " + user.getDisplayName() + " is logged in");


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            db.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getId().equals(user.getUid())) {
                                        Log.d("Firebase", "User: " + document.getData().get("nickname") + " is in group " + document.getData().get("group"));
                                    }
                                    Log.d("Firebase", document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.w("Firebase", "Error getting documents.", task.getException());
                            }
                        }
                    });

            Log.d("Firebase", "DB request created");
        } else {
            // No user is signed in
            createSignInIntent();
        }

        Users.getInstance();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_logout:
                signOutAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                //new AuthUI.IdpConfig.PhoneBuilder().build(),
                //new AuthUI.IdpConfig.FacebookBuilder().build(),
                //new AuthUI.IdpConfig.TwitterBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_logo)      // Set logo drawable
                        .setTheme(R.style.LoginTheme)      // Set theme
                        .build(),
                RC_SIGN_IN);
    }

    public void signOutAccount() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        createSignInIntent();
                    }
                });
    }

    public void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
    }


    protected void setFloatingActionButtonStyleByTabId(final int position) {
        if (position < 0) {
            floatingActionButton.hide();
        }
        else {
            floatingActionButton.show();
        }

        switch(position) {
            case TAB_CLEANING:
                floatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary));
                floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_check, null));
                break;
            default:
                floatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
                floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_icon_add, null));
                break;
        }
    }

    protected void animateFloatingActionButton(final int tabId) {
        floatingActionButton.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(50);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());

        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                setFloatingActionButtonStyleByTabId(tabId);
                // Scale up animation
                ScaleAnimation expand =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                floatingActionButton.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        floatingActionButton.startAnimation(shrink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // Get String data from Intent
            String tabIndex = data.getStringExtra(MainActivity.GOTO_TAB);

            if (!tabIndex.isEmpty()) {
                tabLayout.selectTab(tabLayout.getTabAt(Integer.valueOf(tabIndex)));
            }
        }
    }

/*
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
 */

}
