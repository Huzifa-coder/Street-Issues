package com.barmej.streetissues.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.barmej.streetissues.Fragments.IssuesFragment;
import com.barmej.streetissues.Fragments.MapIssueFragment;
import com.barmej.streetissues.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPagerAdpter mViewPagerAdpter;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar toolbar;
    private FloatingActionButton mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viwe_pager);
        mButton = findViewById(R.id.floatingActionButton);

        mViewPagerAdpter = new ViewPagerAdpter(getSupportFragmentManager());
        mViewPagerAdpter.addFragment(new IssuesFragment());
        mViewPagerAdpter.addFragment(new MapIssueFragment());

        mViewPager.setAdapter(mViewPagerAdpter);
        mTabLayout.setupWithViewPager(mViewPager);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNewIssueActivity.class));
            }
        });

    }

    class ViewPagerAdpter extends FragmentPagerAdapter{

        ArrayList<Fragment> fragments = new ArrayList<>();
        public ViewPagerAdpter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.issues_list);
                case 1:
                    return getString(R.string.issues_on_map);
                default:
                    return null;
            }
        }

        public void addFragment(Fragment fragment){
            fragments.add(fragment);
        }
    }
}
