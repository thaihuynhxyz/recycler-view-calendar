package com.th.calendar;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

/**
 * Implementation of the {@link SnapHelper} supporting calendar style snapping in either vertical or
 * horizontal orientation.
 * <p>
 * <p>
 * <p>
 * CalendarSnapHelper can help achieve a similar behavior to {@link android.widget.CalendarView}.
 * Set both {@link RecyclerView} and the items of the
 * {@link android.support.v7.widget.RecyclerView.Adapter} to have
 * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT} height and width and then attach
 * CalendarSnapHelper to the {@link RecyclerView} using {@link #attachToRecyclerView(RecyclerView)}.
 */
class CalendarSnapHelper extends LinearSnapHelper {

    static final int ITEM_PER_MONTH = 49;

    // Orientation helpers are lazily created per LayoutManager.
    @Nullable
    private OrientationHelper mHorizontalHelper;

    private int mSnapPosition;

    private RecyclerView mRecyclerView;

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager));
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        View mStartMostChildView = null;
        if (layoutManager.canScrollHorizontally()) {
            mStartMostChildView = findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }

        if (mStartMostChildView == null) {
            return RecyclerView.NO_POSITION;
        }
        final int centerPosition = layoutManager.getPosition(mStartMostChildView) / ITEM_PER_MONTH * ITEM_PER_MONTH + ITEM_PER_MONTH / 2;
        if (centerPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        final boolean forwardDirection;
        if (layoutManager.canScrollHorizontally()) {
            forwardDirection = velocityX > 0;
        } else {
            forwardDirection = velocityY > 0;
        }
        boolean reverseLayout = false;
        if ((layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            RecyclerView.SmoothScroller.ScrollVectorProvider vectorProvider =
                    (RecyclerView.SmoothScroller.ScrollVectorProvider) layoutManager;
            PointF vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1);
            if (vectorForEnd != null) {
                reverseLayout = vectorForEnd.x < 0 || vectorForEnd.y < 0;
            }
        }
        mSnapPosition = reverseLayout
                ? (forwardDirection ? centerPosition - ITEM_PER_MONTH : centerPosition)
                : (forwardDirection ? centerPosition + ITEM_PER_MONTH : centerPosition);
        return mSnapPosition;
    }

    /**
     * Return the child view that is currently closest to the center of this parent.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The relevant {@link OrientationHelper} for the attached {@link RecyclerView}.
     * @return the child view that is currently closest to the center of this parent.
     */
    @Nullable
    private View findCenterView(RecyclerView.LayoutManager layoutManager,
                                OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        final int center;
        if (layoutManager.getClipToPadding()) {
            center = helper.getStartAfterPadding() + helper.getTotalSpace() / 2;
        } else {
            center = helper.getEnd() / 2;
        }
        int absClosest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            if (layoutManager.getPosition(child) % ITEM_PER_MONTH != ITEM_PER_MONTH / 2) continue;
            int childCenter = helper.getDecoratedStart(child)
                    + (helper.getDecoratedMeasurement(child) / 2);
            int absDistance = Math.abs(childCenter - center);

            // if child center is closer than previous closest, set it as closest
            if (absDistance < absClosest) {
                absClosest = absDistance;
                closestChild = child;
            }
        }
        return closestChild;
    }

    /**
     * Return the child view that is currently closest to the start of this parent.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param helper        The relevant {@link OrientationHelper} for the attached {@link RecyclerView}.
     * @return the child view that is currently closest to the start of this parent.
     */
    @Nullable
    private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        int startest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);
            int childStart = helper.getDecoratedStart(child);

            // if child is more to start than previous closest, set it as closest
            if (childStart < startest) {
                startest = childStart;
                closestChild = child;
            }
        }
        return closestChild;
    }

    @NonNull
    private OrientationHelper getHorizontalHelper(
            @NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    int getSnapPosition() {
        return mSnapPosition;
    }

    void next() {
        mSnapPosition -= ITEM_PER_MONTH;
        mRecyclerView.smoothScrollToPosition(mSnapPosition);
    }

    void prev() {
        mSnapPosition += ITEM_PER_MONTH;
        mRecyclerView.smoothScrollToPosition(mSnapPosition);
    }
}
