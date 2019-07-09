package com.practicaltraining.render.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;


import com.practicaltraining.render.R;
import com.practicaltraining.render.callbacks.LongClickCallBack;


import java.util.Timer;
import java.util.TimerTask;


public class LongClickButton extends android.support.v7.widget.AppCompatButton {
    private static String TAG = "LongClickButton";
    private Timer timer;
    private TimerTask tt;
    // 上下左右分别为 1  2  3  4
    private int buttonType;
    private LongClickCallBack longClickCallBack;

    public void setLongClickCallBack(LongClickCallBack longClickCallBack) {
        this.longClickCallBack = longClickCallBack;
    }

    public LongClickButton(Context context) {
        super(context);
    }

    public LongClickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongClickButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.LongClickButton, defStyleAttr, 0);
        buttonType = array.getInt(R.styleable.LongClickButton_buttonType, -1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.performClick();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                timer = new Timer();
                tt = new TimerTask() {
                    @Override
                    public void run() {
                        if (buttonType != -1) {
                            longClickCallBack.onLongClick();
                        }
                    }
                };
                Log.d(TAG, "ACTION_MOVE");
                timer.schedule(tt, 0, 50);
                break;
            case MotionEvent.ACTION_UP:
                timer.cancel();
                timer = null;
                tt.cancel();
                tt = null;
                Log.d(TAG, "ACTION_UP");
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            default:
                break;
        }
        return true;
    }
}
