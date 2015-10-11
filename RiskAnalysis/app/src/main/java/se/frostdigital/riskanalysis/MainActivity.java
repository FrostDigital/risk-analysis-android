package se.frostdigital.riskanalysis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private RiskAnalysisView riskAnalysisView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        riskAnalysisView = (RiskAnalysisView) findViewById(R.id.riskAnalysis);
        riskAnalysisView.setSelectedRowAndColumn(3,3);
    }

}
