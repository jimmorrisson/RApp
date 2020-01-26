package com.example.radioaktywne;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

public class RATextView extends AppCompatTextView {
    private Paint paint = new Paint();
    private float strokeWidth = 10.0f;

    public RATextView(Context context) {
        super(context);
        init();
    }

    public RATextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RATextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint.setColor(getResources().getColor(R.color.RAGreenColor));                    // set the color
        paint.setStrokeWidth(strokeWidth);               // set the size
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
        canvas.drawLine(0, height, width, height, paint);
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        paint.setStrokeWidth(strokeWidth);
    }
}
