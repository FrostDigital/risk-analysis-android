package se.frostdigital.riskanalysis;

import android.content.Context;
import android.graphics.Canvas;
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
public class RiskAnalysisAreasSuperView extends View {

    private int mWidth, mHeight;


    protected int mRows, mColumns;
    //selection
    protected int mSelectedRow, mSelectedColumn;
    //Inner sizes and calculations
    protected Rect[][] mAreasMatrix;


    //Reusable vars
    protected Path mReusablePath;
    protected Rect mReusableBounds;

    public RiskAnalysisAreasSuperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mReusableBounds = new Rect();
        mReusablePath = new Path();
    }

    ////
    //// Public methods
    ////

    /**
     * Allows to set selected row and column based on user interaction
     * @param row - row to select
     * @param column - column to select
     */
    public void setSelectedRowAndColumn(int row, int column) {
        boolean shouldInvalidate = false;
        if (row >= 0 && row < this.getRows() && mSelectedRow != row) {
            mSelectedRow = row;
            shouldInvalidate = true;
        }
        if (column >= 0 && column < this.getColumns() && mSelectedColumn != column) {
            mSelectedColumn = column;
            shouldInvalidate = true;
        }
        if (shouldInvalidate) {
            invalidate();
        }
    }


    ////
    //// Getters and Setters
    ////

    public int getRows() {
        return mRows;
    }

    public void setRows(int mRows) {
        this.mRows = mRows;
        initAreasMatrixWithWidthAndHeight(mWidth, mHeight);
    }

    public int getColumns() {
        return mColumns;
    }

    public void setColumns(int mColumns) {
        this.mColumns = mColumns;
        initAreasMatrixWithWidthAndHeight(mWidth, mHeight);
    }

    /**
     * Allows to init / invalidate Areas matrix
     * @param width - width of the view
     * @param height - height of the view
     */
    protected void initAreasMatrixWithWidthAndHeight(int width, int height) {
        if (this.getRows() == 0 || this.getColumns() == 0) {
            return;
        }
        int rowHeight = height / this.getRows();
        int colWidth = width / this.getColumns();
        mAreasMatrix = new Rect[this.getRows()][this.getColumns()];
        int top, left;
        for (int rowIndex = 0; rowIndex < this.getRows(); rowIndex++) {
            for (int colIndex = 0; colIndex < this.getColumns(); colIndex++) {
                top = rowHeight * rowIndex;
                left = colWidth * colIndex;
                mAreasMatrix[rowIndex][colIndex] = new Rect(left, top, left + colWidth - 1, top + rowHeight - 1);
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
    }
}
