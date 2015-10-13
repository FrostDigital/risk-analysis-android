package se.frostdigital.riskanalysis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private RiskAnalysisBackgroundView riskAnalysisBackgroundView;
    private RiskAnalysisOverlayView overlayView;
    private Spinner projectsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        setupRiskAnalysisViews();
        setupProjectsSpinner();
    }

    private void initUI() {
        riskAnalysisBackgroundView = (RiskAnalysisBackgroundView) findViewById(R.id.riskAnalysis);
        overlayView = (RiskAnalysisOverlayView) findViewById(R.id.riskAnalysisOverlay);

        projectsSpinner = (Spinner) findViewById(R.id.projectsSpinner);
    }

    private void setupRiskAnalysisViews() {
        //Do any needed setup of Risk Analysis views here
    }

    private void setupProjectsSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.projects_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectsSpinner.setAdapter(adapter);
        projectsSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Random r = new Random();
        int randomRow = r.nextInt(getResources().getInteger(R.integer.risk_rows));
        int randomColumn = r.nextInt(getResources().getInteger(R.integer.risk_columns));
        riskAnalysisBackgroundView.setSelectedRowAndColumn(randomRow,randomColumn);
        overlayView.setSelectedRowAndColumn(randomRow,randomColumn);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Do nothing
    }
}
