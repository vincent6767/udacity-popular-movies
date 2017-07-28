package com.example.android.popularmovies.adapterviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.entities.Review;

import java.util.List;

/**
 * Created by vincent on 7/24/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Review> mReviews;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        mContext = context;
        mReviews = reviews;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // We are sure that the holder is the instance of ReviewViewHolder.
        ReviewViewHolder reviewViewHolder = (ReviewViewHolder) holder;
        reviewViewHolder.mAuthorView.setText(mReviews.get(position).getAuthor());
        reviewViewHolder.mContentView.setText(mReviews.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return (mReviews == null) ? 0 : mReviews.size();
    }
    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mAuthorView;
        TextView mContentView;
        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthorView = itemView.findViewById(R.id.tv_review_author);
            mContentView = itemView.findViewById(R.id.tv_review_content);
        }
        // We don't any click listener here.
    }

    public void setReviews(List<Review> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }
    public void addReview(Review review) {
        mReviews.add(review);
        notifyItemInserted(mReviews.size() - 1);
    }
    public void removeReview(int position) {
        if (position > mReviews.size() || position < 0) {
            throw new IllegalArgumentException();
        }
        mReviews.remove(position);
        notifyItemRemoved(mReviews.size());
    }
}
