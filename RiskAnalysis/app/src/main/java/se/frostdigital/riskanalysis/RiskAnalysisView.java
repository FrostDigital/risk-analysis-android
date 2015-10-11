package se.frostdigital.riskanalysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * RiskAnalysis
 * Created by Sergii Nezdolii on 22/09/15.
 *
 * Copyright (c) 2015 Frost°. All rights reserved.
 */
public class RiskAnalysisView extends View {

    enum AreaType {
        GREEN(R.color.green),
        YELLOW(R.color.yellow),
        RED(R.color.red);

        private int color;

        AreaType(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }
    }

    private int columns;
    private int rows;
    private int selectedRow, selectedColumn;
    private AreaType[][] colorsMatrix;

    private Paint greenPaint, yellowPaint, redPaint, separatorPaint;
    private List<Paint> areasPaints;
    public RiskAnalysisView(Context context, AttributeSet attrs) {
        super(context, attrs);
        columns = 6;
        rows = 6;
        initPaints();
        colorsMatrix = new AreaType[][]
                {
                {AreaType.RED,      AreaType.RED,       AreaType.RED,       AreaType.RED,       AreaType.RED,       AreaType.RED},
                {AreaType.YELLOW,   AreaType.YELLOW,    AreaType.RED,       AreaType.RED,       AreaType.RED,       AreaType.RED},
                {AreaType.YELLOW,   AreaType.YELLOW,    AreaType.YELLOW,    AreaType.YELLOW,    AreaType.RED,       AreaType.RED},
                {AreaType.GREEN,    AreaType.YELLOW,    AreaType.YELLOW,    AreaType.YELLOW,    AreaType.YELLOW,    AreaType.RED},
                {AreaType.GREEN,    AreaType.GREEN,     AreaType.YELLOW,    AreaType.YELLOW,    AreaType.YELLOW,    AreaType.YELLOW},
                {AreaType.GREEN,    AreaType.GREEN,     AreaType.GREEN,     AreaType.GREEN,     AreaType.YELLOW,    AreaType.YELLOW},
                };
    }

    public void setSelectedRowAndColumn(int row, int column) {
        if (row >= 0 && row < this.rows) {
            this.selectedRow = row;
        }
        if (column >= 0 && column < this.columns) {
            this.selectedColumn = column;
        }
        invalidate();
    }

    private void initPaints() {
        greenPaint = new Paint();
        greenPaint.setColor(getResources().getColor(AreaType.GREEN.getColor()));
        greenPaint.setStyle(Paint.Style.FILL);

        yellowPaint = new Paint(greenPaint);
        yellowPaint.setColor(getResources().getColor(AreaType.YELLOW.getColor()));

        redPaint = new Paint(yellowPaint);
        redPaint.setColor(getResources().getColor(AreaType.RED.getColor()));

        separatorPaint = new Paint();
        separatorPaint.setColor(getResources().getColor(R.color.white));
        separatorPaint.setStyle(Paint.Style.FILL);

        areasPaints = new ArrayList<Paint>();
        areasPaints.add(greenPaint);
        areasPaints.add(yellowPaint);
        areasPaints.add(redPaint);
    }

    private Rect getRectWithinWidthAndHeightForRowAndColumn(int width, int height, int row, int column) {
        //First check that rows and columns exist
        if (row < 0 || column < 0) {
            return new Rect(0, 0, 0, 0);
        }
        int w = width / this.columns;
        int h = height / this.rows;
        int left = w*column;
        int top = h*row;

        return new Rect(left, top, left+w-1, top+h-1);
    }

    private Paint getPaintForRowAndColumn(int row, int column) {
        Paint p = areasPaints.get(colorsMatrix[row][column].ordinal());
        p.setAlpha(shouldBeDisplayedAsSelectedBasedOnRowAndColumn(row, column) ? 150 : 255);
        return p;
    }

    private boolean shouldBeDisplayedAsSelectedBasedOnRowAndColumn(int row, int column) {
        return      row == selectedRow && column <= selectedColumn
                ||  row <= selectedRow && column == selectedColumn;

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = getWidth();
        int height = getHeight();
        canvas.drawRect(0, 0, getWidth(), getHeight(), separatorPaint);
        for (int row = 0; row < this.rows; row++) {
            for (int col = 0; col < this.columns; col++) {
                canvas.drawRect(getRectWithinWidthAndHeightForRowAndColumn(width, height, row, col), getPaintForRowAndColumn(row, col));
            }
        }
    }
}
