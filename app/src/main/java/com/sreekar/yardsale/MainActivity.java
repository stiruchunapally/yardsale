package com.sreekar.yardsale;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.sreekar.yardsale.fragment.MyItemsFragment;
import com.sreekar.yardsale.fragment.PurchasedItemsFragment;
import com.sreekar.yardsale.fragment.RecentItemsFragment;

/**
 * This activity is shown after the user successfully logs in. This page shows three tabs,
 * Recent, Purchased, and My Items. It also shows the items donated by various users,
 * a menu in the top right corner which allows you to log out of the app and a button
 * in the bottom right corner which allows you to donate an item. The initial landing tab
 * is the Recent tab.
 */

//Made by Sreekar Tiruchunapally
public class MainActivity extends BaseActivity implements View.OnClickListener {

    // Initialize variables
    private ViewPager viewPager;
    private FloatingActionButton donateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        viewPager = (ViewPager) findViewById(R.id.container);
        donateButton = (FloatingActionButton) findViewById(R.id.fab_new_post);

        // Setup click listeners
        donateButton.setOnClickListener(this);

        // Create the adapter that will return a fragment for each section and setup the ViewPager
        viewPager.setAdapter(new ItemsFragmentPagerAdapter(getSupportFragmentManager()));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    // Create options menu in top right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * OnClick handler for donate button, start donate activity
     */
    @Override
    public void onClick(View v) {
        Intent donateIntent = new Intent(MainActivity.this, DonateActivity.class);
        startActivity(donateIntent);
    }

    /**
     * When logout button is clicked, logout the user, when about button is clicked
     * go to AboutActivity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else if (i == R.id.about) {
            startActivity(new Intent(this, AboutActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Fragement adapter to create Recent, Purchased and MyItems fragements
     */
    public class ItemsFragmentPagerAdapter extends FragmentPagerAdapter {
        public ItemsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // Create fragments
        private final Fragment[] mFragments = new Fragment[] {
                new RecentItemsFragment(),
                new PurchasedItemsFragment(),
                new MyItemsFragment(),
        };

        // Locate Strings for tab titles from strings.xml
        private final String[] mFragmentNames = new String[] {
                getString(R.string.heading_recent),
                getString(R.string.heading_favorites),
                getString(R.string.heading_my_items)
        };

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return mFragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentNames[position];
        }
    }

}
