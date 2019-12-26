package com.example.radioaktywne;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class RATextViewHorizontal extends AppCompatTextView {
    private Paint paint = new Paint();
    private int drawOffset = 5;

    public RATextViewHorizontal(Context context) {
        super(context);
        init();
    }

    public RATextViewHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RATextViewHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setColor(getResources().getColor(R.color.RABlueColor));
        paint.setStrokeWidth(5.f);               // set the size
        paint.setDither(true);                    // set the dither to true
        paint.setStyle(Paint.Style.STROKE);       // set to STOKE
        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        paint.setPathEffect(new CornerPathEffect(paint.getStrokeWidth()));  // set the path effect when they join.
        paint.setAntiAlias(true);                         // set anti alias so it smooths
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.measure(0,0);
        int height = this.getMeasuredHeight();
        int width = this.getMeasuredWidth();
        float halfHeight = height / 2.f;
        int paddingLeft = this.getPaddingLeft() - drawOffset;
        int paddingRight = this.getPaddingRight() - drawOffset;
        canvas.drawLine(0, halfHeight, paddingLeft, halfHeight, paint);
        canvas.drawLine(width - paddingRight, halfHeight, width, halfHeight, paint);
    }
}
