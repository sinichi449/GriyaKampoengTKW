package net.bagusekasaputra.griyakampoengtkw.ui

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class DisabledSwipeViewPager(
    ctx: Context,
    attrs: AttributeSet?
): ViewPager(ctx, attrs) {

    private val isPagingEnabled = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(ev)
    }
}