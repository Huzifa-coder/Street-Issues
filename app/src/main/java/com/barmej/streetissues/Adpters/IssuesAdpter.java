package com.barmej.streetissues.Adpters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.barmej.streetissues.Objects.Issue;
import com.barmej.streetissues.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class IssuesAdpter extends RecyclerView.Adapter<IssuesAdpter.ViewHolder>{

    private ArrayList<Issue> issues;
    private OnIssueClickListener mOnIssueClickListener;

    public interface  OnIssueClickListener {
        void onIssueClickListener(Issue issue);
    }

    public IssuesAdpter(ArrayList<Issue> issues, OnIssueClickListener mOnIssueClickListener) {
        this.mOnIssueClickListener = mOnIssueClickListener;
        this.issues = issues;
    }

    @NonNull
    @Override
    public IssuesAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_issue, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuesAdpter.ViewHolder viewHolder, int position) {
        viewHolder.bind(issues.get(position));
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private Issue issue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_issue_image_view);
            textView = itemView.findViewById(R.id.item_issue_text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnIssueClickListener.onIssueClickListener(issue);
                }
            });
        }

        public void bind(Issue issue){
            this.issue = issue;
            textView.setText(issue.getTitle());
            Glide.with(imageView)
                    .load(issue.getPhoto())
                    .centerCrop()
                    .into(imageView);
        }
    }
}
