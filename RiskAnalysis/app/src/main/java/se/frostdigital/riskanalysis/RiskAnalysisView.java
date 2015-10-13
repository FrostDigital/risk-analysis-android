package se.frostdigital.riskanalysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

/**
 * RiskAnalysis
 * Created by Sergii Nezdolii on 22/09/15.
 *
 * Copyright (c) 2015 Frost°. All rights reserved.
 */
public class RiskAnalysisView extends RiskAnalysisAreasSuperView {

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

    private AreaType[][] colorsMatrix;

    private Paint greenPaint, yellowPaint, redPaint, separatorPaint;
    private List<Paint> areasPaints;
    public RiskAnalysisView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    private void initPaints() {
        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

    private Paint getPaintForRowAndColumn(int row, int column) {
        Paint p;
        if (row < colorsMatrix.length && column < colorsMatrix[row].length) {
            p = areasPaints.get(colorsMatrix[row][column].ordinal());
        } else {
            p = separatorPaint;
        }
        p.setAlpha(shouldBeDisplayedAsSelectedBasedOnRowAndColumn(row, column) ? 150 : 255);
        return p;
    }

    private boolean shouldBeDisplayedAsSelectedBasedOnRowAndColumn(int row, int column) {
        return      row == mSelectedRow && column <= mSelectedColumn
                ||  row <= mSelectedRow && column == mSelectedColumn;

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), separatorPaint);
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getColumns(); col++) {
                canvas.drawRect(mAreasMatrix[row][col], getPaintForRowAndColumn(row, col));
            }
        }
    }
}
