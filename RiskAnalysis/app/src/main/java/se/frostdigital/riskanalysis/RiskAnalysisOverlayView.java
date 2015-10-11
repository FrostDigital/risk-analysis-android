package se.frostdigital.riskanalysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * RiskAnalysis
 * Created by Sergii Nezdolii on 11/10/15.
 *
 * Copyright (c) 2015 Frost°. All rights reserved.
 */
public class RiskAnalysisOverlayView extends View {

    private Paint mPointerPaint, mTextPaint;
    private int rows, columns;
    private int mWidth, mHeight;

    //selection
    private int mSelectedRow, mSelectedColumn;

    //Inner sizes and calculations
    private Rect[][] mAreasMatrix;

    //Reusable vars
    Path mReusablePath;
    Rect mReusableBounds;

    public RiskAnalysisOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TODO: make customizable via xml attributes.
        rows = 6;
        columns = 6;
        initPaints();
        mReusableBounds = new Rect();
    }

    /**
     * Allows to set selected row and column based on user interaction
     * @param row
     * @param column
     */
    public void setSelectedRowAndColumn(int row, int column) {
        boolean shouldInvalidate = false;
        if (row >= 0 && row < this.rows && mSelectedRow != row) {
            mSelectedRow = row;
            shouldInvalidate = true;
        }
        if (column >= 0 && column < this.columns && mSelectedColumn != column) {
            mSelectedColumn = column;
            shouldInvalidate = true;
        }
        if (shouldInvalidate) {
            invalidate();
        }
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

    private void initAreasMatrixWithWidthAndHeight(int width, int height) {
        int rowHeight = height / this.rows;
        int colWidth = width / this.columns;
        mAreasMatrix = new Rect[this.rows][this.columns];
        int top, left;
        for (int rowIndex = 0; rowIndex < this.rows; rowIndex++) {
            for (int colIndex = 0; colIndex < this.columns; colIndex++) {
                top  = rowHeight * rowIndex;
                left = colWidth * colIndex;
                mAreasMatrix[rowIndex][colIndex] = new Rect(left, top, left+colWidth-1, top+rowHeight-1);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Lazy init of areas
        if (mWidth != getWidth() || mHeight != getHeight()) {
            mWidth = getWidth();
            mHeight = getHeight();
            initAreasMatrixWithWidthAndHeight(mWidth, mHeight);
        }
        drawPointer(canvas);
    }

    private void drawPointer(Canvas canvas) {
        Rect selectedArea = mAreasMatrix[mSelectedRow][mSelectedColumn];
        float radius = selectedArea.width() < selectedArea.height() ? selectedArea.width() / 2 : selectedArea.height() / 2;
        canvas.drawCircle(selectedArea.centerX(), selectedArea.centerY(), radius, mPointerPaint);
        String textToDraw = String.format("%d:%d", mSelectedRow, mSelectedColumn);
        mTextPaint.getTextBounds(textToDraw, 0, textToDraw.length(), mReusableBounds);
        canvas.drawText(textToDraw, selectedArea.centerX(), selectedArea.centerY()+mReusableBounds.height()/2.0f, mTextPaint);
    }
}
