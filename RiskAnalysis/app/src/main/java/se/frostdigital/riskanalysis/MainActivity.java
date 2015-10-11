package se.frostdigital.riskanalysis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private RiskAnalysisView riskAnalysisView;
    private RiskAnalysisOverlayView overlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        riskAnalysisView = (RiskAnalysisView) findViewById(R.id.riskAnalysis);
        overlayView = (RiskAnalysisOverlayView) findViewById(R.id.riskAnalysisOverlay);

        riskAnalysisView.setSelectedRowAndColumn(3,3);
        overlayView.setSelectedRowAndColumn(3,3);
    }

}
