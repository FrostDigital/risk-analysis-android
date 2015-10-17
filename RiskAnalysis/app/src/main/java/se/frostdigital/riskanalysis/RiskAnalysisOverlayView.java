package se.frostdigital.riskanalysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * RiskAnalysis
 * Created by Sergii Nezdolii on 11/10/15.
 *
 * Copyright (c) 2015 Frost°. All rights reserved.
 */
public class RiskAnalysisOverlayView extends RiskAnalysisAreasSuperView {

    private Paint mPointerPaint, mTextPaint;
    private GestureDetector mGestureDetector;

    public RiskAnalysisOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        initGestureDetector();
    }

    ////
    //// Init methods and helpers
    ////

    private void initPaints() {
        mPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerPaint.setColor(getResources().getColor(R.color.blue));
        mPointerPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.white));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(30.0f);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPointer(canvas);
    }

    private void drawPointer(Canvas canvas) {
        if (mAreasMatrix == null) {
            return;
        }
        Rect selectedArea = mAreasMatrix[mSelectedRow][mSelectedColumn];
        float radius = selectedArea.width() < selectedArea.height() ? selectedArea.width() / 2.0f : selectedArea.height() / 2.0f;
        canvas.drawCircle(selectedArea.centerX(), selectedArea.centerY(), radius, mPointerPaint);
        String textToDraw = String.format("%d:%d", mSelectedRow, mSelectedColumn);
        mTextPaint.getTextBounds(textToDraw, 0, textToDraw.length(), mReusableBounds);
        canvas.drawText(textToDraw, selectedArea.centerX(), selectedArea.centerY() + mReusableBounds.height() / 2.0f, mTextPaint);
    }

    ////
    //// Handling Touch events
    ////

    private class RiskAnalysisGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            boolean shouldAllowGesture = isInSelectedArea(event.getX(), event.getY());
            Log.v(this.getClass().getName(), "isInSelectedArea: " + (shouldAllowGesture ? "YES" : "NO"));

            return shouldAllowGesture;
        }
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(RiskAnalysisOverlayView.this.getContext(), new RiskAnalysisGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP) {
                result = updateSelectionForCoordinates(event.getX(), event.getY());
            }
        }
        return result;
    }

    private boolean isInSelectedArea(float x, float y) {
        Rect selectedRect = mAreasMatrix[mSelectedRow][mSelectedColumn];
        return selectedRect.contains((int)x, (int)y);
    }

    private boolean updateSelectionForCoordinates(float x, float y) {
        if (x > getWidth() || y > getHeight()) {
            return false;
        }
        for (int row = 0; row < mAreasMatrix.length; row++) {
            for (int col = 0; col < mAreasMatrix[row].length; col++) {
                if (mAreasMatrix[row][col].contains((int) x, (int) y)) {
                    setSelectedRowAndColumn(row, col);
                    return true;
                }
            }
        }

        return false;
    }
}
