package net.bagusekasaputra.griyakampoengtkw.presentation.util;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.bagusekasaputra.griyakampoengtkw.presentation.R;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SwipeActionCallbackRecyclerView extends ItemTouchHelper.SimpleCallback {

    private OnSwipedListener listener;
    private Context context;


    public SwipeActionCallbackRecyclerView(Context context, OnSwipedListener listener) {
        super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);

        this.listener = listener;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        int rightBackgroundColor = ContextCompat.getColor(this.context, R.color.abang); // Delete background
        int leftBackgroundColor = ContextCompat.getColor(this.context, R.color.secondaryLightColor); // Edit background
        int tintActionColor = ContextCompat.getColor(this.context, R.color.white);

        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                // Edit
                .addSwipeRightBackgroundColor(leftBackgroundColor) // Edit
                .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                .setSwipeRightActionIconTint(tintActionColor)
                // Delete
                .addSwipeLeftBackgroundColor(rightBackgroundColor)
                .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                .setSwipeLeftActionIconTint(tintActionColor)

                .create()
                .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public interface OnSwipedListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction);
    }
}
