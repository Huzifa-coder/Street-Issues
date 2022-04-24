package com.barmej.streetissues.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.streetissues.Activities.DeteilsActivity;
import com.barmej.streetissues.Objects.Issue;
import com.barmej.streetissues.Adpters.IssuesAdpter;
import com.barmej.streetissues.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class IssuesFragment extends Fragment implements IssuesAdpter.OnIssueClickListener {

    public static final String ISSUE_INTENT_NAME = "issue";
    private RecyclerView mRecyclerView;
    private IssuesAdpter issuesAdpter;
    private ArrayList<Issue> issues;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issues_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        issues = new ArrayList<>();
        issuesAdpter = new IssuesAdpter(issues, IssuesFragment.this);

        final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("issues").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                issues.clear();
                if (error == null){
                    for (QueryDocumentSnapshot snapshot : value) {
                        issues.add(snapshot.toObject(Issue.class));
                    }
                    mRecyclerView.setAdapter(issuesAdpter);
                }else{
                    Snackbar.make(getView(), R.string.erore_to_informiton,BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });

        firebaseFirestore.collection("issues").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                issues.clear();
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        issues.add(snapshot.toObject(Issue.class));
                    }
                    mRecyclerView.setAdapter(issuesAdpter);
                }else{
                    Snackbar.make(getView(), R.string.erore_to_informiton,BaseTransientBottomBar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onIssueClickListener(Issue issue) {
        if (issue != null) {
            Intent intent = new Intent(getContext(), DeteilsActivity.class);
            intent.putExtra(ISSUE_INTENT_NAME, issue);
            startActivity(intent);
        }
    }
}
