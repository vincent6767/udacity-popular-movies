package com.example.android.popularmovies.adapterviews;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.listeners.OnLoadMoreListener;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.entities.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private enum VIEW_TYPE{
        VIEW_ITEM(1), VIEW_PROG(0);
        private int numVal;
        VIEW_TYPE(int numVal) {
            this.numVal = numVal;
        }
        public int getNumVal() {
            return numVal;
        }

    }

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalitemCount;
    private boolean mLoading;
    private Context mContext;
    private List<Movie> mMovies;
    private MovieAdapterOnClickHandler mClickHandler;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView mMoviesListView;
    private RecyclerView.OnScrollListener mOnScrollListener;


    public MovieAdapter(Context context, ArrayList<Movie> movies, MovieAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mMovies = movies;
        this.mClickHandler = clickHandler;
    }
    public MovieAdapter(Context context, ArrayList<Movie> movies, MovieAdapterOnClickHandler clickHandler, RecyclerView rv) {
        this(context, movies, clickHandler);
        mMoviesListView = rv;
        if (rv.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager layoutManager = (GridLayoutManager) rv.getLayoutManager();
            rv.addOnScrollListener(mOnScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalitemCount = layoutManager.getItemCount();
                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    // Load more should only run if there are Movies inside it, in not loading state,
                    // and the total item count is less than last visible item + visible threshold.
                    if (mMovies != null && !mLoading && totalitemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        mLoading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMovies.get(position) != null ? VIEW_TYPE.VIEW_ITEM.getNumVal() : VIEW_TYPE.VIEW_PROG.getNumVal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_TYPE.VIEW_ITEM.getNumVal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
            viewHolder = new MovieViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            ((MovieViewHolder) holder).tv_release_date.setText(String.valueOf(mMovies.get(position).getYearReleaseDate()));
            Picasso.with(mContext).load(mMovies.get(position).getFullThumbnailImageUrl()).into(((MovieViewHolder) holder).iv_movie_thumbnail);
        } else if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).pv_load_more_indicator.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return (mMovies == null) ? 0 : mMovies.size();
    }

    public void setLoaded() {
        mLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
    }

    public void setMovieData(List<Movie> movies) {
        if (movies == null) {
            mMovies.clear();
        } else {
            mMovies.addAll(movies);
        }
        notifyDataSetChanged();
    }
    public void addMovieData(Movie movie) {
        mMovies.add(movie);
        notifyDataSetChanged();
    }
    public void addMovies(List<Movie> movies) {
        for (Movie movie : movies) {
            addMovieData(movie);
        }
    }
    public void removeMoviesData(int position) {
        if (position > mMovies.size() || position < 0) {
            throw new IllegalArgumentException();
        }
        mMovies.remove(position);
        notifyItemRemoved(mMovies.size());
    }
    public void emptyMoviesData() {
        mMovies = new ArrayList<>();
        notifyDataSetChanged();
    }
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView iv_movie_thumbnail;
        TextView tv_release_date;

        public MovieViewHolder(View itemView) {
            super(itemView);
            // Cache all view components
            iv_movie_thumbnail = itemView.findViewById(R.id.iv_movie_thumbnail);
            tv_release_date= itemView.findViewById(R.id.tv_release_date);
            // Add listener to the view.
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mMovies.get(getAdapterPosition()));
        }
    }

    private class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pv_load_more_indicator;
        public ProgressViewHolder(View view) {
            super(view);
            pv_load_more_indicator = view.findViewById(R.id.pb_load_more_indicator);
        }
    }
}
