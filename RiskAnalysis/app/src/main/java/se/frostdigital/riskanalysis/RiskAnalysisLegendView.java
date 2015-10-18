package se.frostdigital.riskanalysis;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * RiskAnalysis
 * Created by Sergii Nezdolii on 18/10/15.
 *
 * Copyright (c) 2015 Frost°. All rights reserved.
 */
public class RiskAnalysisLegendView extends View {

    private static final float titleWeight = 0.6f;

    private String[] xTitles, yTitles;
    private String mXAxisTitle, mYAxisTitle;
    private float mAxisOffset;
    private int mWidth, mHeight;
    private RectF[] xAreas, yAreas;
    private RectF xAxisTitleArea, yAxisTitleArea;
    private Paint mTextPaint, mAxisTitlePaint;

    private Rect mReusableBounds;
    private Paint fitTextPaint;

    public RiskAnalysisLegendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray customAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RiskAnalysisLegendView,0, 0);
        try {
            mAxisOffset = customAttrs.getDimension(R.styleable.RiskAnalysisLegendView_axisOffset, 0.0f);
        } finally {
            customAttrs.recycle();
        }

        mReusableBounds = new Rect();

        initTitles();
        initPaints();
    }

    private void initTitles() {
        if (isInEditMode()) {
            xTitles = new String[]{"1","2","3","4","5","6"};
            yTitles = new String[]{"1","2","3","4","5","6"};
        } else {
            xTitles = getResources().getStringArray(R.array.legend_x_axis);
            yTitles = getResources().getStringArray(R.array.legend_y_axis);
        }
        mXAxisTitle = getResources().getString(R.string.x_axis_title);
        mYAxisTitle = getResources().getString(R.string.y_axis_title);
    }

    private void initPaints() {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.black));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextScaleX(1.0f);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mAxisTitlePaint = new Paint(mTextPaint);
        fitTextPaint = new Paint(mTextPaint);
    }

    private void initTitlesAreasWithWidthAndHeight(int width, int height) {
        initXTitlesAreasWithWidthAndHeight(width, height);
        initYTitlesAreasWithWidthAndHeight(width, height);
    }

    private void initXTitlesAreasWithWidthAndHeight(int width, int height) {
        //Generate X Axis
        if (xTitles != null && xTitles.length > 0) {
            //First, setup Axis title rect
            float xAxisTitleHeight = mAxisOffset * titleWeight;
            float xAxisWidth = width - mAxisOffset;
            xAxisTitleArea = new RectF(mAxisOffset, height - xAxisTitleHeight, width, height);

            xAreas = new RectF[xTitles.length];
            float xTitleWidth = xAxisWidth / xTitles.length;
            float top = height - mAxisOffset;
            float left;
            for (int i = 0; i < xTitles.length; i++) {
                left = xTitleWidth * i + mAxisOffset;
                xAreas[i] = new RectF(left, top, left + xTitleWidth, top + mAxisOffset - xAxisTitleHeight);
            }

            //Calculating text size
            float xTextSize = getTextSizeForTextInRect(xTitles[0], xAreas[0]);
            for (int i = 1; i < xTitles.length; i++) {
                xTextSize = Math.min(xTextSize, getTextSizeForTextInRect(xTitles[i], xAreas[i]));
            }
            mTextPaint.setTextSize(xTextSize);
        } else {
            xAxisTitleArea = new RectF(mAxisOffset, height - mAxisOffset, width, height);
        }
        mAxisTitlePaint.setTextSize(getTextSizeForTextInRect(mXAxisTitle, xAxisTitleArea));
    }

    private void initYTitlesAreasWithWidthAndHeight(int width, int height) {
        //Generate Y Axis
        if (yTitles != null && yTitles.length > 0) {
            float yAxisWidth = height - mAxisOffset;
            float yValueWidth = yAxisWidth / yTitles.length;
            float yTitleHeight = mAxisOffset * titleWeight;
            yAxisTitleArea = new RectF(mAxisOffset, 0, height, yTitleHeight);
            yAreas = new RectF[yTitles.length];
            float top = yTitleHeight;
            float left;
            for (int i = 0; i < yTitles.length; i++) {
                left = yValueWidth * i + mAxisOffset;
                yAreas[i] = new RectF(left, top, left + yValueWidth, mAxisOffset);
            }

            //Calculating text size
            float yTextSize = getTextSizeForTextInRect(yTitles[0], yAreas[0]);
            for (int i = 1; i < yTitles.length; i++) {
                yTextSize = Math.min(yTextSize, getTextSizeForTextInRect(yTitles[i], yAreas[i]));
            }
            mTextPaint.setTextSize(Math.min(mTextPaint.getTextSize(), yTextSize));
        } else {
            yAxisTitleArea = new RectF(mAxisOffset, 0, height, mAxisOffset);
        }
        mAxisTitlePaint.setTextSize(Math.min(mAxisTitlePaint.getTextSize(), getTextSizeForTextInRect(mYAxisTitle, yAxisTitleArea)));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth != getWidth() || mHeight != getHeight()) {
            mWidth = getWidth();
            mHeight = getHeight();
            initTitlesAreasWithWidthAndHeight(mWidth, mHeight);
        }
        drawXAxisTitles(canvas);
        drawYAxisTitles(canvas);
    }

    private void drawXAxisTitles(Canvas canvas) {
        if (xAxisTitleArea != null) {
            drawTextInRectWithPaint(canvas, mXAxisTitle, xAxisTitleArea, mAxisTitlePaint);
        }

        if (xAreas == null) {
            return;
        }

        for (int i = 0; i < xAreas.length; i++) {
            drawTextInRectWithPaint(canvas, xTitles[i], xAreas[i], mTextPaint);
        }
    }

    private void drawYAxisTitles(Canvas canvas) {
        if (yAreas == null) {
            return;
        }
        canvas.save();
        canvas.rotate(-90, mHeight / 2.0f, mHeight / 2.0f);
        if (yAxisTitleArea != null) {
            drawTextInRectWithPaint(canvas, mYAxisTitle, yAxisTitleArea, mAxisTitlePaint);
        }
        for (int i = 0; i < yAreas.length; i++) {
            drawTextInRectWithPaint(canvas, yTitles[i], yAreas[i], mTextPaint);
        }
        canvas.restore();
    }

    private void drawTextInRectWithPaint(Canvas canvas, String text, RectF rect, Paint paint) {
        mTextPaint.getTextBounds(text, 0, text.length(), mReusableBounds);
        canvas.drawText(text, rect.centerX(), rect.centerY() + mReusableBounds.height() / 2.0f, paint);
    }

    ////
    //// Layout Helpers
    ////

    /**
     * Allows to calculate desired text size to fit whole text in one line in the rect.
     * @param text - text to count size for
     * @param textRect - rect to fit text in
     * @return textSize to use to fit text in best way
     */
    protected float getTextSizeForTextInRect(String text, RectF textRect) {
        float textSize = 1;
        fitTextPaint.setTextSize(textSize);
        fitTextPaint.setTextScaleX(1.0f);
        fitTextPaint.getTextBounds(text, 0, text.length(), mReusableBounds);
        while (mReusableBounds.height() < textRect.height()*.7 && mReusableBounds.width() < textRect.width()) {
            textSize++;
            fitTextPaint.setTextSize(textSize);
            fitTextPaint.getTextBounds(text, 0, text.length(), mReusableBounds);
        }

        return --textSize;
    }
}
