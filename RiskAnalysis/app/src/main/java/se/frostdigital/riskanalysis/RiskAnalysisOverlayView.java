package se.frostdigital.riskanalysis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
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
    private boolean mShouldShowBubble;
    private Bitmap mBubbleBitmap;

    public RiskAnalysisOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();
        initGestureDetector();
        mBubbleBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bubble);
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

    private String getTextToDraw() {
        return String.format("%d:%d", mSelectedRow, mSelectedColumn);
    }

    private void setShouldShowBubble(boolean showBubble) {
        if (mShouldShowBubble != showBubble) {
            mShouldShowBubble = showBubble;
            invalidate();
        }
    }

    ////
    //// Draw methods
    ////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAreasMatrix == null) {
            return;
        }
        drawPointer(canvas);
        if (mShouldShowBubble) {
            drawBubble(canvas);
        }
    }

    private void drawPointer(Canvas canvas) {
        Rect selectedArea = mAreasMatrix[mSelectedRow][mSelectedColumn];
        float radius = Math.min(selectedArea.width(), selectedArea.height()) / 2.0f;
        //If bubble is to be shown, circle should be bigger 20%
        if (mShouldShowBubble) {
            radius *= 1.2f;
        }
        canvas.drawCircle(selectedArea.centerX(), selectedArea.centerY(), radius, mPointerPaint);
        drawTextInCenteredXY(canvas, getTextToDraw(), selectedArea.centerX(), selectedArea.centerY());
    }

    private void drawTextInCenteredXY(Canvas canvas, String textToDraw, float x, float y) {
        mTextPaint.getTextBounds(textToDraw, 0, textToDraw.length(), mReusableBounds);
        canvas.drawText(textToDraw, x, y + mReusableBounds.height() / 2.0f, mTextPaint);
    }

    private RectF getBubbleRect() {
        RectF bubbleArea = new RectF(mAreasMatrix[mSelectedRow][mSelectedColumn]);
        bubbleArea.offset(0, -bubbleArea.height()*1.5f);
        bubbleArea.bottom += bubbleArea.height()*0.25f;
        return bubbleArea;
    }

    private void drawBubble(Canvas canvas) {
        RectF bubbleRect = getBubbleRect();
        canvas.drawBitmap(mBubbleBitmap, null, bubbleRect, null);
        drawTextInCenteredXY(canvas, getTextToDraw(), bubbleRect.centerX(), bubbleRect.centerY()-10);
    }

    ////
    //// Handling Touch events
    ////

    private class RiskAnalysisGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            //Should allow gestures which start in selected area
            return isInSelectedArea(event.getX(), event.getY());
        }
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(RiskAnalysisOverlayView.this.getContext(), new RiskAnalysisGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        //This happens in case if initial down action occurred
        if (result && event.getAction() == MotionEvent.ACTION_DOWN) {
            setShouldShowBubble(true);
        }
        if (!result) {
            //Check if moved out of current area and need to update selection
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                result = updateSelectionForCoordinates(event.getX(), event.getY());
            }
            //Check if gesture is to be finised, hide bubble then
            if (event.getAction() == MotionEvent.ACTION_UP) {
                setShouldShowBubble(false);
                result = true;
            }
        }
        return result;
    }

    ////
    //// Touch Helpers
    ////

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
