package com.example.hca127.greenfood.fragments;

import android.support.design.widget.NavigationView;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.hca127.greenfood.MainActivity;
import com.example.hca127.greenfood.R;
import com.example.hca127.greenfood.objects.Diet;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class ResultFragment extends Fragment {
    private PieChart mResultPieChart;
    private BarChart mResultBarChart;
    private TextView mResultText;
    private float mUserCarbon;
    private float mAverageCarbon = 1517.5f;
    private float mLowCarbonPercentage = 0.9f;
    private float mAverageCarbonPercentage = 1.1f;
    private Button mGetSuggestion;
    private Button mToggleNumbers;
    private Boolean mShowNumbers = false;

    private Diet mDiet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        mDiet = ((MainActivity)getActivity()).getLocalUserDiet();
        mUserCarbon = mDiet.getUserDietEmission(); //insert calculated mCarbonSaved in tC02e

        mResultText = view.findViewById(R.id.resultText);
        mToggleNumbers = view.findViewById(R.id.toggleNumbers);

        if(mUserCarbon == 0) {
            mResultText.setText(R.string.result_no_carbon);
        } else if (mUserCarbon < mAverageCarbon*mLowCarbonPercentage) {
            String temp = String.format(getResources().getString(R.string.result_low_carbon),
                    String.valueOf(mDiet.getUserDietEmission()),
                    String.valueOf(mAverageCarbon));
            mResultText.setText(temp);
        } else if (mUserCarbon < mAverageCarbon*mAverageCarbonPercentage) {
            String temp = String.format(getResources().getString(R.string.result_average_carbon),
                    String.valueOf(mDiet.getUserDietEmission()),
                    String.valueOf(mAverageCarbon));
            mResultText.setText(temp);
        } else {
            String temp = String.format(getResources().getString(R.string.result_high_carbon),
                    String.valueOf(mDiet.getUserDietEmission()),
                    String.valueOf(mAverageCarbon));
            mResultText.setText(temp);
        }

        mResultPieChart = view.findViewById(R.id.resultPieChart);
        setupPieChart(mResultPieChart);

        mResultBarChart = view.findViewById(R.id.resultBarChart);
        setupBarChart(mResultBarChart);

        mGetSuggestion = view.findViewById(R.id.getSuggestion);
        mGetSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SuggestionFragment()).addToBackStack(null).commit();
                ((NavigationView)getActivity().findViewById(R.id.navigation_view)).setCheckedItem(R.id.menu_suggestion);
            }
        });

        mToggleNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowNumbers = !mShowNumbers;
                setupPieChart(mResultPieChart);
            }
        });

        return view;
    }
    private void setupPieChart(PieChart chart) {
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i <mDiet.getSize(); i++) {
            if (mDiet.getIngUserCo2Emission(i) != 0) {
                pieEntries.add(new PieEntry( mDiet.getIngUserCo2Emission(i), mDiet.getFoodName(i)));
            }
        }

        if (mUserCarbon == 0)
            pieEntries.add(new PieEntry(20000f, "Blood!"));

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(12f);

        if (!mShowNumbers)
            dataSet.setDrawValues(false);

        chart.getLegend().setEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        chart.getDescription().setEnabled(false);

        chart.setData(data);
        chart.animateY(1200);
        chart.invalidate();
    }

    private void setupBarChart(BarChart chart) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, mDiet.getUserDietEmission()));
        entries.add(new BarEntry(1, mAverageCarbon));

        BarDataSet barDataSet = new BarDataSet(entries, "BarDataSet");
        barDataSet.setValueTextSize(12f);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData suggestionData = new BarData(barDataSet);
        chart.getXAxis().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getDescription().setEnabled(false);

        chart.setData(suggestionData);
        chart.animateY(1200);
        chart.invalidate();
    }
}
