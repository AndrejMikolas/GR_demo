package sk.andrejmik.gr_demo.utils

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

class RecyclerTouchListener(context: Context?, recyclerView: RecyclerView?, private var clickListener: ClickListener) :
        RecyclerView.OnItemTouchListener
{
    private var gestureDetector: GestureDetector = GestureDetector(context, object : SimpleOnGestureListener()
    {
        override fun onSingleTapUp(e: MotionEvent): Boolean
        {
            return true
        }
    })

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent)
    {

    }

    override fun onInterceptTouchEvent(@NonNull recyclerView: RecyclerView, @NonNull motionEvent: MotionEvent): Boolean
    {
        val child = recyclerView.findChildViewUnder(motionEvent.x, motionEvent.y)
        if (child != null && gestureDetector.onTouchEvent(motionEvent))
        {
            clickListener.onClick(child, recyclerView.getChildPosition(child))
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(b: Boolean)
    {
    }

    interface ClickListener
    {
        fun onClick(view: View?, position: Int)
    }
}