package com.example.twittertest.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.example.twittertest.R;
import com.example.twittertest.presenter.OnPictureClickListener;
import com.squareup.picasso.Picasso;

public class PhotoViewHolder extends RecyclerView.ViewHolder {

    private ImageView mImageView;

    public PhotoViewHolder(final View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.photoViewItem_image);
    }

    public void bind(final @NonNull String url, final OnPictureClickListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Picasso.with(itemView.getContext())
            .load(url)
            .into(mImageView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPictureClicked(url);
            }
        });
    }
}
