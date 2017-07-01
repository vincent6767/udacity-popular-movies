package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by vincent on 7/1/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context mContext;
    private ArrayList<Movie> mMovies;

    public MovieAdapter(Context context, ArrayList<Movie> movies) {
        this.mContext = context;
        this.mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        holder.tv_movie_title.setText(mMovies.get(position).getTitle());
        holder.tv_release_date.setText(mMovies.get(position).getReleaseDate());
        // Load image from URL and put it on ImageView
        Picasso.with(mContext).load(mMovies.get(position).getThumbnailImageUrl()).into(holder.iv_movie_thumbnail);
    }

    @Override
    public int getItemCount() {
        return (mMovies == null) ? 0 : mMovies.size();
    }

    public void setMovieData(ArrayList<Movie> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_movie_thumbnail;
        TextView tv_movie_title;
        TextView tv_release_date;

        public MovieViewHolder(View itemView) {
            super(itemView);
            //Cache all view components
            iv_movie_thumbnail = (ImageView) itemView.findViewById(R.id.iv_movie_thumbnail);
            tv_movie_title= (TextView) itemView.findViewById(R.id.tv_movie_title);
            tv_release_date= (TextView) itemView.findViewById(R.id.tv_release_date);
        }
    }
}
