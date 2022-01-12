package com.wind.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

/**
 * created by wind on 12/10/20:10:39 AM
 */
public class CenterImageSpan extends ImageSpan {

    public CenterImageSpan(@NonNull Context context, @DrawableRes int resourceId
                     ) {
        super(context,resourceId);
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        try {
            Drawable d = getDrawable();
            canvas.save();
            int transY = 0;
            transY = ((bottom-top) - d.getBounds().bottom) / 2+top;
            canvas.translate(x, transY);
            d.draw(canvas);
            canvas.restore();
        } catch (Exception e) {
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        try {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        } catch (Exception e) {
            return 20;
        }
    }
}
