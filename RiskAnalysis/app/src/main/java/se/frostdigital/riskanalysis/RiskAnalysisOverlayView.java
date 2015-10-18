package se.frostdigital.riskanalysis;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
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

    private float mPointeSizeOnDragMultiplier;

    public RiskAnalysisOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
        initPaints();
        initGestureDetector();
        mBubbleBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bubble);
    }

    ////
    //// Init methods and helpers
    ////

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray customAttrs = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RiskAnalysisOverlayView,0, 0);
        try {
            mPointeSizeOnDragMultiplier = customAttrs.getFloat(R.styleable.RiskAnalysisOverlayView_pointerSizeOnDragMultiplier, 1.0f);
        } finally {
            customAttrs.recycle();
        }
    }

    private void initPaints() {
        mPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointerPaint.setColor(getResources().getColor(R.color.blue));
        mPointerPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(getResources().getColor(R.color.white));
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(50.0f);
        mTextPaint.setTextScaleX(1.0f);
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
        RectF selectedArea = mAreasMatrix[mSelectedRow][mSelectedColumn];
        float radius = Math.min(selectedArea.width(), selectedArea.height()) / 2.0f;
        //If bubble is to be shown, circle should be bigger
        if (mShouldShowBubble) {
            canvas.drawCircle(selectedArea.centerX(), selectedArea.centerY(), radius * mPointeSizeOnDragMultiplier, mPointerPaint);
        } else {
            canvas.drawCircle(selectedArea.centerX(), selectedArea.centerY(), radius, mPointerPaint);
            drawTextInRectWithdXdY(canvas, getTextToDraw(), selectedArea, 0, 0);
        }
    }

    private void drawTextInRectWithdXdY(Canvas canvas, String textToDraw, RectF rect, float dX, float dY) {
        String text = getTextToDraw();
        mTextPaint.getTextBounds(textToDraw, 0, textToDraw.length(), mReusableBounds);
        canvas.drawText(textToDraw, rect.centerX()+dX, rect.centerY() + dY + mReusableBounds.height() / 2.0f, mTextPaint);
    }

    private RectF getBubbleRect() {
        RectF bubbleArea = new RectF(mAreasMatrix[mSelectedRow][mSelectedColumn]);
        bubbleArea.offset(0, -bubbleArea.height()*1.5f);
        bubbleArea.bottom += bubbleArea.height()*0.25f;
        return bubbleArea;
    }

    private void drawBubble(Canvas canvas) {
        RectF bubbleRect = getBubbleRect();
        //Hacky solution to allow drawing bubble out of bounds
        boolean shouldExtendBounds = bubbleRect.top <= 0;
        if (shouldExtendBounds) {
            canvas.save();
            // allow drawing out of bounds vertically
            Rect clipBounds = canvas.getClipBounds();
            clipBounds.inset(0, (int) bubbleRect.top);
            canvas.clipRect(clipBounds, Region.Op.REPLACE);
        }

        canvas.drawBitmap(mBubbleBitmap, null, bubbleRect, null);
        drawTextInRectWithdXdY(canvas, getTextToDraw(), bubbleRect, 0, - 10);

        if (shouldExtendBounds) {
            canvas.restore();
        }
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
        RectF selectedRect = mAreasMatrix[mSelectedRow][mSelectedColumn];
        return selectedRect.contains(x, y);
    }

    private boolean updateSelectionForCoordinates(float x, float y) {
        if (x > getWidth() || y > getHeight()) {
            return false;
        }
        for (int row = 0; row < mAreasMatrix.length; row++) {
            for (int col = 0; col < mAreasMatrix[row].length; col++) {
                if (mAreasMatrix[row][col].contains(x, y)) {
                    setSelectedRowAndColumn(row, col);
                    return true;
                }
            }
        }

        return false;
    }
}
