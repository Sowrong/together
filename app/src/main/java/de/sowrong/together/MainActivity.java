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
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

import de.sowrong.together.data.User;
import de.sowrong.together.data.Users;
import de.sowrong.together.ui.SettingsActivity;
import de.sowrong.together.ui.calendar.DetailsCalenderEntryActivity;
import de.sowrong.together.ui.calendar.NewEditCalenderEntryActivity;
import de.sowrong.together.ui.cleaning.CleaningFragment;
import de.sowrong.together.ui.init.CreateGroupActivity;
import de.sowrong.together.ui.init.JoinGroupActivity;
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

    private boolean joinedGroup;

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
        joinedGroup = false;
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true); TODO Enable
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!joinedGroup) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) {
                // User is signed in
                Log.d("Firebase", "User: " + firebaseUser.getDisplayName() + " is logged in");

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference usersReference = db.getReference().child("users/" + firebaseUser.getUid() + "/");

                usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            User user = dataSnapshot.getValue(User.class);

                            if (user == null) { // user doesn't exists in database
                                user = new User();
                                user.setId(firebaseUser.getUid());
                                user.setName(firebaseUser.getDisplayName());
                                usersReference.setValue(user);
                                joinedGroup = false;
                                onResume();
                            }

                            if (user.getGroupId() == null || user.getGroupId().isEmpty()) {
                                createNewOrJoinGroupInterface();
                            } else {
                                joinedGroup = true;
                                createDefaultInterface();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("MAIN", "failed to update users", error.toException());
                    }
                });
            } else {
                // No user is signed in
                createSignInIntent();
            }
        }
    }

    private void createNewOrJoinGroupInterface() {
        setContentView(R.layout.activity_select_join_new_group);

        findViewById(R.id.newGroup).setOnClickListener(view ->
        {
            Intent intent = new Intent(context, CreateGroupActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.joinGroup).setOnClickListener(view ->
        {
            Intent intent = new Intent(context, JoinGroupActivity.class);
            startActivity(intent);
        });
    }

    private void createDefaultInterface() {
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

        Users.getInstance().addUserDataChangedListeners(usersMap -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String username = usersMap.get(firebaseUser.getUid()).getName();
            String email = firebaseUser.getEmail();

            ((TextView) navigationView.findViewById(R.id.navNameTextView)).setText(username);
            ((TextView) navigationView.findViewById(R.id.navEmailTextView)).setText(email);
        });

        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) item -> {
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
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        new AppBarConfiguration.Builder(
                R.id.nav_cleaning, R.id.nav_calendar, R.id.nav_wallet, R.id.nav_shopping_list)
                .setDrawerLayout(drawer)
                .build();

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabs);

        viewPager.setAdapter(new ViewPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_leave_group:
                leaveGroup();
                return true;
            case R.id.action_logout:
                signOutAccount();
                joinedGroup = false;
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(context, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void leaveGroup() {
        User self = Users.getInstance().getUserById(Users.getOwnId());

        joinedGroup = false;

        if (self != null) {
            self.setGroupId("");

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users/" + self.getId());
            ref.setValue(self);
        }

        onResume();
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (joinedGroup == false) {
            onResume();
            return;
        }

        if (resultCode == Activity.RESULT_OK) {
            // Get String data from Intent
            String tabIndex = data.getStringExtra(MainActivity.GOTO_TAB);

            if (!tabIndex.isEmpty()) {
                tabLayout.selectTab(tabLayout.getTabAt(Integer.valueOf(tabIndex)));
            }
        }
    }
}
