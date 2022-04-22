package com.familyon;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wstatsapp.R;
import com.familyon.SPHelpher.SharedData;
import com.familyon.server.ApiClient;
import com.familyon.server.ApiInterface;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrackingFragment2 extends Fragment implements View.OnClickListener {

    LineChart chart1;
    ProgressDialog progressDialog;
    RecyclerView reportsRecycler;
    LinearLayout noTrackingLayout, trackingLayout;
    Button subscriberName, currentTracking, dailyTracking, weeklyTracking;
    ImageView clearReports;
    JSONArray weeklyArray, dailyArray, currentArray;
    ReportsRecyclerViewAdapter adapter;
    int height = 0;
    String adapterType="now";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tracking, container, false);
        reportsRecycler = root.findViewById(R.id.reportsRecycler);
        noTrackingLayout = root.findViewById(R.id.noTrackingLayout);
        trackingLayout = root.findViewById(R.id.trackingLayout);
        chart1 = root.findViewById(R.id.chart1);
        subscriberName = root.findViewById(R.id.subscriberName);
        clearReports = root.findViewById(R.id.clearReports);
        weeklyTracking = root.findViewById(R.id.weeklyTracking);
        currentTracking = root.findViewById(R.id.currentTracking);
        dailyTracking = root.findViewById(R.id.dailyTracking);
        clearReports.setOnClickListener(this);
        weeklyTracking.setOnClickListener(this);
        dailyTracking.setOnClickListener(this);
        currentTracking.setOnClickListener(this);
        reportsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetReports();

        if (!adapterType.equalsIgnoreCase("now")) {
            reportsRecycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    calculateRecyclerViewFullHeight();
                }
            });
        }
        //setData();
    }

    public void GetReports() {
        // if (Utility.isNetworkAvailable(getActivity())) {
        progressDialog = Utility.showProgress(getActivity());
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        // JSONObject model = new JSONObject();
        try {

            HashMap<String, String> request = new HashMap<>();
            request.put("mobile_number", SharedData.getTrackingNumber(getActivity()));
            request.put("user_id", SharedData.getUserID(getActivity()));
            Call<JSONObject> call = apiService.GetReports(request);
            // showProgress();
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    // hideProgress();
                    try {
                        Utility.hideProgress(progressDialog);
                        if (response.code() == 200 && response.body() != null && response.body().getString("status").equals("1")) {
                            Gson gson = new Gson();

                            try {

                                JSONArray jsonArray = response.body().getJSONArray("result");

                                if (jsonArray.length() > 0) {
                                    weeklyArray = new JSONArray();
                                    dailyArray = new JSONArray();
                                    currentArray = new JSONArray();
                                    JSONObject reportObject = new JSONObject();
                                    String lastStatus = "";
                                    ArrayList<Entry> entries = new ArrayList<>();
                                    int count = 0;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        try {
                                            if (!lastStatus.equals(jsonArray.getJSONObject(i).getString("status"))) {
                                                if (jsonArray.getJSONObject(i).getString("status").equals("Offline")) {
                                                    reportObject.put("offline_date", jsonArray.getJSONObject(i).getString("created"));
                                                    reportObject.put("offline_time", jsonArray.getJSONObject(i).getString("last_seen"));

                                                } else {
                                                    reportObject.put("online_date", jsonArray.getJSONObject(i).getString("created"));
                                                    reportObject.put("online_time", jsonArray.getJSONObject(i).getString("last_seen"));
                                                    if (reportObject.has("online_date") && reportObject.has("offline_date")) {
                                                        long millis = Long.parseLong(reportObject.getString("offline_time").split("\\.")[0]) * 1000L - Long.parseLong(reportObject.getString("online_time").split("\\.")[0]) * 1000L;
                                                        reportObject.put("duration", millis);
                                                    }
                                                    weeklyArray.put(reportObject);
                                                    if (Utility.getFormatedDateTime(new Date(), "yyyy-MM-dd").equals(jsonArray.getJSONObject(i).getString("created"))) {
                                                        dailyArray.put(reportObject);
                                                    }
                                                    if (currentArray.length() == 0 && Utility.getFormatedDateTime(new Date(), "yyyy-MM-dd").equals(jsonArray.getJSONObject(i).getString("created"))) {
                                                        currentArray.put(reportObject);
                                                    }
                                                    reportObject = new JSONObject();
                                                }
                                                lastStatus = jsonArray.getJSONObject(i).getString("status");


                                            }

                                        } catch (Exception ex) {

                                        }
                                    }

                                    if (reportObject.length() > 0) {
                                        weeklyArray.put(reportObject);
                                        if (Utility.getFormatedDateTime(new Date(), "yyyy-MM-dd").equals(reportObject.getString("offline_date"))) {
                                            dailyArray.put(reportObject);
                                        }
                                        if (currentArray.length() == 0) {
                                            currentArray.put(reportObject);
                                        }
                                        reportObject = new JSONObject();
                                    }
                                    setData(weeklyArray);
                                    adapter = new ReportsRecyclerViewAdapter(currentArray,"now");
                                    reportsRecycler.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                    noTrackingLayout.setVisibility(View.GONE);
                                    trackingLayout.setVisibility(View.VISIBLE);
                                    CurrentView();
                                } else {
                                    noTrackingLayout.setVisibility(View.VISIBLE);
                                    trackingLayout.setVisibility(View.GONE);
                                }
                            } catch (Exception ex) {

                            }

                        } else if (response.code() == 500) {
                            if (response.errorBody() != null) {
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    } catch (Exception ex) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    // hideProgress();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception ex) {
            Utility.hideProgress(progressDialog);
        }
        //}

    }

    private void setData(JSONArray reportsArray) {
        subscriberName.setText(SharedData.getTrackingName(getActivity()));
        List<Entry> entries = new ArrayList<>();
        int count = 0;
        for (int i = reportsArray.length() - 1; i >= 0; i--) {
            try {

                float xVal = (float) (count+1);
                float yVal = 0;
                if (reportsArray.getJSONObject(i).has("duration")) {
                    yVal = (float) reportsArray.getJSONObject(i).getLong("duration");
                } else {
                    yVal = 0;
                }
                entries.add(new Entry(xVal, yVal));
                count++;
            } catch (Exception ex) {

            }
        }

        // sort by x-value
        Collections.sort(entries, new EntryXComparator());

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(entries, " ");

        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.disableDashedLine();
        set1.setDrawValues(false);
        set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set1.setColor(getResources().getColor(R.color.colorAccent));
        // create a data object with the data sets
        LineData data = new LineData(set1);

        // set data
        chart1.setData(data);
        Description dx = new Description();
        dx.setText("");
        chart1.setDescription(dx);
        chart1.getAxisLeft().setDrawGridLines(false);
        chart1.getXAxis().setDrawGridLines(false);
        chart1.getAxisRight().setDrawGridLines(false);
        chart1.setDrawBorders(false);
        chart1.getAxisRight().setDrawLabels(false);
        chart1.getAxisLeft().setDrawLabels(false);
        chart1.getAxisRight().setDrawAxisLine(false);
        chart1.getAxisLeft().setDrawAxisLine(false);
        XAxis xAxis = chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorAccent));
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(getResources().getColor(R.color.colorWhite));
        xAxis.setDrawLabels(false);
//        xAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return String.valueOf((int) Math.ceil(value));
//            }
//        });

        chart1.getLegend().setEnabled(false);
        chart1.setTouchEnabled(false);
        chart1.invalidate();
        //chart1.setBackgroundColor(R.color.colorPrimary);
        chart1.refreshDrawableState();
    }

    public void ClearReports() {
        // if (Utility.isNetworkAvailable(getActivity())) {
        progressDialog = Utility.showProgress(getActivity());
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        // JSONObject model = new JSONObject();
        try {

            HashMap<String, String> request = new HashMap<>();
            request.put("user_id", SharedData.getUserID(getActivity()));

            Call<JSONObject> call = apiService.ClearReports(request);
            // showProgress();
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    // hideProgress();
                    try {
                        Utility.hideProgress(progressDialog);
                        if (response.code() == 200 && response.body() != null && response.body().getString("status").equals("1")) {
                            noTrackingLayout.setVisibility(View.VISIBLE);
                            trackingLayout.setVisibility(View.GONE);
                        } else if (response.code() == 500) {
                            if (response.errorBody() != null) {
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    } catch (Exception ex) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    // hideProgress();
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception ex) {
            Utility.hideProgress(progressDialog);
        }
        //}

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearReports:
                ClearReports();
                setData(new JSONArray());
                break;
            case R.id.dailyTracking:
                //setData(dailyArray);
                //reportsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapterType="daily";
                adapter = new ReportsRecyclerViewAdapter(dailyArray,"daily");
                reportsRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                currentTracking.setClickable(true);
                dailyTracking.setClickable(false);
                weeklyTracking.setClickable(true);
                currentTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_gray));
                currentTracking.setTextColor(getResources().getColor(R.color.colorPrimary));

                dailyTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_primary));
                dailyTracking.setTextColor(getResources().getColor(R.color.colorWhite));

                weeklyTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_gray));
                weeklyTracking.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.weeklyTracking:
                //setData(weeklyArray);
                //reportsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapterType="weekly";
                adapter = new ReportsRecyclerViewAdapter(weeklyArray,"weekly");
                reportsRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                currentTracking.setClickable(true);
                dailyTracking.setClickable(true);
                weeklyTracking.setClickable(false);
                currentTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_gray));
                currentTracking.setTextColor(getResources().getColor(R.color.colorPrimary));

                dailyTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_gray));
                dailyTracking.setTextColor(getResources().getColor(R.color.colorPrimary));

                weeklyTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_primary));
                weeklyTracking.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
            case R.id.currentTracking:
                //setData(currentArray);
                adapter = new ReportsRecyclerViewAdapter(currentArray,"now");
                reportsRecycler.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                CurrentView();
                break;
            default:
                break;

        }
    }

    private void CurrentView() {
        currentTracking.setClickable(false);
        dailyTracking.setClickable(true);
        weeklyTracking.setClickable(true);
        currentTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_primary));
        currentTracking.setTextColor(getResources().getColor(R.color.colorWhite));

        dailyTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_gray));
        dailyTracking.setTextColor(getResources().getColor(R.color.colorPrimary));

        weeklyTracking.setBackground(getResources().getDrawable(R.drawable.circular_border_gray));
        weeklyTracking.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    protected void calculateRecyclerViewFullHeight() {
        height=0;
        reportsRecycler.post(new Runnable() {
            @Override
            public void run() {
               // Log.show("myRView.post " + myRView.getChildCount());
                for (int idx = 0; idx < adapter.getItemCount(); idx++ ) {

                    height += 220;
                }
                ViewGroup.LayoutParams params = reportsRecycler.getLayoutParams();
                params.height = height;
                reportsRecycler.setLayoutParams(params);
            }
        });

    }

}
