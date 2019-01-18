package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;
/**
 * To solve the problem of nested sliding of listView and srcollView
 */
public class NestScrollView extends ScrollView {

    public NestScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 因为ACTION_DOWN始终无法进入ScrollView的onTouchEvent，
            // 但是ScrollView的滚动需要在ACTION_DOWN时做一些准备
            onTouchEvent(ev);
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            //can do something
        }
        return super.onTouchEvent(ev);
    }


}
