package com.barmej.streetissues.Activities;

import static com.barmej.streetissues.Fragments.IssuesFragment.ISSUE_INTENT_NAME;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.barmej.streetissues.Objects.Issue;
import com.barmej.streetissues.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DeteilsActivity extends AppCompatActivity{

    private Issue mIssue;
    private Toolbar mToolbar;
    private ImageView mImageView;
    private TextView mDetailsTextView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_details);

        mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView = findViewById(R.id.imageView_issue_details);
        mDetailsTextView = findViewById(R.id.textView_detiales_issue_details);

        if (getIntent() != null && getIntent().getParcelableExtra(ISSUE_INTENT_NAME) != null) {
            mIssue = getIntent().getParcelableExtra(ISSUE_INTENT_NAME);
            mToolbar.setTitle(mIssue.getTitle());
            Glide.with(this)
                    .load(mIssue.getPhoto())
                    .centerCrop()
                    .into(mImageView);
            mDetailsTextView.setText(mIssue.getDetails());

        }
    }


}
