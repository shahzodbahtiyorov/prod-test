package org.finance.ui.ui_helper.card_decaration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CardItemDecoration extends RecyclerView.ItemDecoration {

    private final int space;

    public CardItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();


        if (position == 0) {
            outRect.left = 0;
        } else {
            outRect.left = space;
        }


        if (position == itemCount - 1) {
            outRect.right = 0;
        } else {
            outRect.right = space;
        }


        outRect.top = space;
        outRect.bottom = space;
    }
}

