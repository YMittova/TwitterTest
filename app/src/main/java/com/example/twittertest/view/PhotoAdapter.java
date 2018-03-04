package com.example.twittertest.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.twittertest.R;
import com.example.twittertest.presenter.OnPictureClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuliamittova on 28/02/2018.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private OnPictureClickListener mClickListener;
    private List<String> mPhotoUrls = new ArrayList<>();

    public PhotoAdapter(final @NonNull OnPictureClickListener listener) {
        mClickListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_view_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if (position > mPhotoUrls.size()) {
            return;
        }
        holder.bind(mPhotoUrls.get(position), mClickListener);
    }

    @Override
    public int getItemCount() {
        return mPhotoUrls.size();
    }

    public void setPhotoUrls(final List<String> photoUrls) {
        mPhotoUrls.clear();
        mPhotoUrls.addAll(photoUrls);
    }

    public void clear() {
        mPhotoUrls.clear();
        notifyDataSetChanged();
    }
}
