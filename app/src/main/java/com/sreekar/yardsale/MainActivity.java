package com.sreekar.yardsale;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.sreekar.yardsale.fragment.MyItemsFragment;
import com.sreekar.yardsale.fragment.FavoriteItemsFragment;
import com.sreekar.yardsale.fragment.RecentItemsFragment;

public class MainActivity extends BaseActivity {

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private FloatingActionButton bdonate;


    public void donateButton(){
        bdonate = (FloatingActionButton) findViewById(R.id.fab_new_post);

        bdonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent donateIntent = new Intent(MainActivity.this, DonateActivity.class);
                startActivity(donateIntent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        donateButton();

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new RecentItemsFragment(),
                    new FavoriteItemsFragment(),
                    new MyItemsFragment(),
            };

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
        };

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
