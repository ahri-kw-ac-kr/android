package com.example.jms;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;


public class WeekAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_act);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        BarChart mBarChart = (BarChart) findViewById(R.id.bar);

        mBarChart.addBar(new BarModel("월", 0.5f, Color.parseColor("#CAEBA2")));
        mBarChart.addBar(new BarModel("화", 0.8f, Color.parseColor("#CAEBA2")));
        mBarChart.addBar(new BarModel("수", 1.2f, Color.parseColor("#8CCA45")));
        mBarChart.addBar(new BarModel("목", 2.1f, Color.parseColor("#8CCA45")));
        mBarChart.addBar(new BarModel("금", 3.3f, Color.parseColor("#5F9919")));
        mBarChart.addBar(new BarModel("토", 2.4f, Color.parseColor("#8CCA45")));
        mBarChart.addBar(new BarModel("일", 3.1f, Color.parseColor("#5F9919")));

        mBarChart.startAnimation();

    }
}