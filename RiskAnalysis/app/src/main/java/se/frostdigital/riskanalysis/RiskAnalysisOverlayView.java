package se.frostdigital.riskanalysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

/**
 * RiskAnalysis
 * Created by Sergii Nezdolii on 11/10/15.
 *
 * Copyright (c) 2015 Frost°. All rights reserved.
 */
public class RiskAnalysisOverlayView extends RiskAnalysisAreasSuperView {

    private Paint mPointerPaint, mTextPaint;

    public RiskAnalysisOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //TODO: make customizable via xml attributes.
        setRows(6);
        setColumns(6);
        initPaints();
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
}
