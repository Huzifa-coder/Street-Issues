package com.barmej.streetissues.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.barmej.streetissues.Activities.DeteilsActivity;
import com.barmej.streetissues.Objects.Issue;
import com.barmej.streetissues.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MapIssueFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.mapView_issue_fragment);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        googleMap.setOnInfoWindowClickListener(this);

        firebaseFirestore.collection("issues").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                googleMap.clear();
                for (QueryDocumentSnapshot snapshot : value){
                    Issue issue = snapshot.toObject(Issue.class);
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(issue.getLocation().getLatitude(), issue.getLocation().getLongitude()));
                    options.title(issue.getTitle());
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    Marker marker = googleMap.addMarker(options);
                    marker.setTag(issue);
                }
            }
        });

        firebaseFirestore.collection("issues").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot snapshot : task.getResult()){
                    Issue issue = snapshot.toObject(Issue.class);
                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(issue.getLocation().getLatitude(), issue.getLocation().getLongitude()));
                    options.title(issue.getTitle());
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    Marker marker = googleMap.addMarker(options);
                    marker.setTag(issue);
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
            Intent intent = new Intent(getContext(), DeteilsActivity.class);
            intent.putExtra(IssuesFragment.ISSUE_INTENT_NAME, (Issue) marker.getTag());
            startActivity(intent);
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
