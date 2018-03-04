package com.example.twittertest.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class PhotoItemDecoration extends RecyclerView.ItemDecoration {

    private final int mColumnCount;
    private final int mSpacing;
    private final int mHalfSpacing;

    public PhotoItemDecoration(final int columnCount, final int spacing) {
        super();

        mColumnCount = columnCount;
        mSpacing = spacing;
        mHalfSpacing = spacing / 2;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % mColumnCount;
        if (column == 0) {
            // the left column
            outRect.left = mSpacing;
            outRect.right = mHalfSpacing;
        }
        else if (column == mColumnCount - 1) {
            // the right column
            outRect.left = mHalfSpacing;
            outRect.right = mSpacing;
        }
        else {
            outRect.left = mHalfSpacing;
            outRect.right = mHalfSpacing;
        }

        outRect.top = mHalfSpacing;
        outRect.bottom = mHalfSpacing;
    }
}
