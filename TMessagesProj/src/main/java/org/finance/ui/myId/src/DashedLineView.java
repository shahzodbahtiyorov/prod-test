package org.finance.ui.myId.src;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class DashedLineView extends View {

    private Paint paint;

    public DashedLineView(Context context) {
        super(context);
        init();
    }

    public DashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLACK); // Chiziq rangi
        paint.setStyle(Paint.Style.STROKE); // Chiziq uslubi (faqat chiziq)
        paint.setStrokeWidth(5); // Chiziq qalinligi

        // PathEffect uzilgan chiziqni yaratish uchun
        DashPathEffect dashEffect = new DashPathEffect(new float[]{10, 20}, 0);
        paint.setPathEffect(dashEffect); // PathEffect ni Paint ga o'rnatamiz
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Chiziqni view o'rtasidan chizamiz
        int startX = 0;
        int stopX = getWidth();
        int y = getHeight() / 2;

        // Chiziqni canvasga chizish
        canvas.drawLine(startX, y, stopX, y, paint);
    }
}
