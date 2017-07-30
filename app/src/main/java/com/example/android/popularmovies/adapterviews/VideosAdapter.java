package com.example.android.popularmovies.adapterviews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.entities.Video;

import java.util.List;

/**
 * Created by vincent on 7/29/17.
 */

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Video> mVideos;
    private VideoAdapterOnClickHandler mClickHandler;

    public VideosAdapter(Context context, List<Video> videos, VideoAdapterOnClickHandler clickHandler) {
        mContext = context;
        mVideos = videos;
        mClickHandler = clickHandler;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;

        viewHolder.mVideoName.setText(mVideos.get(position).getName());
    }
    @Override
    public int getItemCount() {
        return mVideos == null ? 0 : mVideos.size() ;
    }
    public interface VideoAdapterOnClickHandler {
        void onClick(Video video);
    }
    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView mVideoThumbnail;
        TextView mVideoName;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mVideoThumbnail = itemView.findViewById(R.id.iv_video_thumbnail);
            mVideoName = itemView.findViewById(R.id.tv_video_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickHandler.onClick(mVideos.get(getAdapterPosition()));
        }
    }
    /*
    * Additional methods for mVideos.
    * */
    public void setVideos(List<Video> videos) {
        mVideos = videos;
        notifyDataSetChanged();
    }
    public void addVideo(Video review) {
        mVideos.add(review);
        notifyItemInserted(mVideos.size() - 1);
    }
    public void removeVideo(int position) {
        if (position > mVideos.size() || position < 0) {
            throw new IllegalArgumentException("Index out of bound.");
        }
        mVideos.remove(position);
        notifyItemRemoved(mVideos.size());
    }
}
