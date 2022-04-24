package com.barmej.streetissues.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.barmej.streetissues.Objects.Issue;
import com.barmej.streetissues.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.installations.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddNewIssueActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_READ_EXTRNAL_STORGE = 0;
    private static final int PERMISSION_FINE_LOACTION = 1;
    private static final int REQUEST_CODE_PHOTO = 2;
    private static final LatLng LOCAL_LOCATION = new LatLng(15.642469979571848, 32.53135538732182);

    private boolean mPermissionReadStorge;
    private boolean mPermissionLoaction;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mSelectedLatLong;
    private Location mLastKnownLocation;
    private GoogleMap mGoogleMap;
    private Uri mUri;

    private ConstraintLayout mConstraintLayout;
    private MaterialButton button;
    private ImageView mImageView;
    private TextInputEditText mTitleEditText;
    private TextInputEditText mDetailsEditText;
    private SupportMapFragment mapFragment;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_issue);
        mDetailsEditText = findViewById(R.id.editText_details_add_new_issue);
        mTitleEditText = findViewById(R.id.editText_title_add_new_issue);
        mImageView = findViewById(R.id.imageView_add_new_issue);
        mConstraintLayout = findViewById(R.id.constraint);
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        button = findViewById(R.id.materialButton);

        onPermissionReadStorage();
        onPermissionLocation();

        mapFragment.getMapAsync(this);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPermissionReadStorge)
                    onSelectPhoto();
                else
                    onPermissionReadStorage();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDetailsEditText.setError(null);
                mTitleEditText.setError(null);
                if (TextUtils.isEmpty(mDetailsEditText.getText())){
                    mDetailsEditText.setError(getString(R.string.details_is_empty));
                }else if (TextUtils.isEmpty(mTitleEditText.getText())){
                    mTitleEditText.setError(getString(R.string.title_is_empty));
                }if (mUri != null){
                    addNewIssueToFireBase();
                }
            }
        });
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    public void addNewIssueToFireBase(){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        final StorageReference photoStorageReference = storageReference.child(UUID.randomUUID().toString());
        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        mDialog = new ProgressDialog(this);
        mDialog.setTitle(getString(R.string.app_name));
        mDialog.setMessage(getString(R.string.upload_photo));
        mDialog.show();

        photoStorageReference.putFile(mUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    photoStorageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                final Issue issue = new Issue();
                                issue.setTitle(mTitleEditText.getText().toString());
                                issue.setDetails(mDetailsEditText.getText().toString());
                                issue.setLocation(new GeoPoint(mSelectedLatLong.latitude, mSelectedLatLong.longitude));
                                issue.setPhoto(task.getResult().toString());
                                firebaseFirestore.collection("issues").add(issue).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()){
                                            Snackbar.make(mConstraintLayout, R.string.issue_completed, BaseTransientBottomBar.LENGTH_SHORT).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                                                @Override
                                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                                    super.onDismissed(transientBottomBar, event);
                                                    finish();
                                                }
                                            }).show();
                                        }else {
                                            Snackbar.make(mConstraintLayout, R.string.issue_uncompleted, BaseTransientBottomBar.LENGTH_SHORT).show();
                                            mDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }else{
                    Snackbar.make(mConstraintLayout, R.string.photo_uncompleted, BaseTransientBottomBar.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_EXTRNAL_STORGE:
                mPermissionReadStorge = false;
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionReadStorge = true;
                }
                break;
            case PERMISSION_FINE_LOACTION:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionLoaction = true;
                    requestDeviceCurrentLocation();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PHOTO && data.getData() != null) {
                mUri = data.getData();
                mImageView.setImageURI(mUri);
            } else {
                Toast.makeText(this, R.string.error_select_photo, Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void onPermissionReadStorage() {
        mPermissionReadStorge = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            mPermissionReadStorge = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ_EXTRNAL_STORGE);
        }
    }

    public void onPermissionLocation() {
        mPermissionLoaction = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mPermissionLoaction = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOACTION);
        }
    }

    public void onSelectPhoto() {
        Intent photo = new Intent(Intent.ACTION_GET_CONTENT);
        photo.setType("image/*");
        photo.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(photo, getString(R.string.select_photo)), REQUEST_CODE_PHOTO);
    }

    @SuppressLint("MissingPermission")
    private void requestDeviceCurrentLocation() {
        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    mLastKnownLocation = location;
                    mSelectedLatLong = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mSelectedLatLong, 15));
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(mSelectedLatLong);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    mGoogleMap.addMarker(markerOptions);
                } else {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LOCAL_LOCATION, 15));
                }
            }

            ;
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mPermissionLoaction){
            requestDeviceCurrentLocation();
        }
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mSelectedLatLong = latLng;
                mGoogleMap.clear();
                MarkerOptions options = new MarkerOptions();
                options.position(mSelectedLatLong);
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mGoogleMap.addMarker(options);
            }
        });
    }

}
