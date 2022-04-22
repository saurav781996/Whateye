package com.familyon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.billingclient.api.SkuDetails;
import com.example.wstatsapp.R;
import com.google.gson.Gson;
import com.familyon.SPHelpher.SharedData;
import com.familyon.server.ApiClient;
import com.familyon.server.ApiInterface;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Utility {

    public static List<SkuDetails> skuDetails;
    public static ProgressDialog showProgress(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        try {
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();

        } catch (Exception ex) {

        }
        return progressDialog;
    }

    public static void hideProgress(ProgressDialog progressDialog) {
        try {
            progressDialog.dismiss();
        } catch (Exception ex) {

        }
    }

    public static String ConvertMillisTodateString(String millis) {
        try {
            Date dt = new Date(Long.parseLong(millis.split("\\.")[0])*1000L);
           // dt.setTime(Long.parseLong(millis.split("\\.")[0]));
            //return getFormatedDateTime(dt,"hh:mm a");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh:mm a", Locale.getDefault());
            return sdf.format(dt);
        } catch (Exception ex) {
            return "Not available";
        }
    }

    public static String ConvertDateToString(Date dt) {
        try {
            return getFormatedDateTime(dt,"yyyy-MM-dd HH:mm:ss");
        } catch (Exception ex) {
            return "Not available";
        }
    }

    public static long convertDateToMillis(String dateString) {

        try {
            String format = "yyyy-MM-dd hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dt= sdf.parse(dateString);
            return dt.getTime();
        } catch (Exception ex) {
            return 0;
        }
    }


    public static String getFormatedDateTime(Date dt,String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(dt);
        } catch (Exception ex) {
            return "Not available";
        }
    }
    public static String formatDate(String dateString)
    {
        try {
            String format = "yyyy-MM-dd hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dt= sdf.parse(dateString);
            return getFormatedDateTime(dt,"dd MMM yyyy");
        } catch (Exception ex) {
            return "";
        }
    }


    public static long parseDate(String dateString)
    {
        try {
            String format = "yyyy-MM-dd hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dt= sdf.parse(dateString);
            return dt.getTime();
        } catch (Exception ex) {
            return 0;
        }
    }
    public static void ShowAlert(Context ctx, String text) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.alert_dialog, null);
        dialog.setView(customView);

        TextView message = (TextView) customView.findViewById(R.id.alertMesage);
        message.setText(text);
        Button ok = (Button) customView.findViewById(R.id.ok);
        final AlertDialog alertDialogAndroid = dialog.create();
        alertDialogAndroid.setCanceledOnTouchOutside(false);
        alertDialogAndroid.setCancelable(false);
        dialog.setCancelable(false);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
            }
        });
        alertDialogAndroid.show();
    }

    public static void SavePayment(final String planId,final Activity context) {

        final ProgressDialog progressDialog = Utility.showProgress(context);
        ApiInterface apiService = ApiClient.getClient(context).create(ApiInterface.class);
        try {

            HashMap<String, String> request = new HashMap<>();
            request.put("user_id", SharedData.getUserID(context));
            request.put("play_id", planId);

            Call<JSONObject> call = apiService.SuccessPayment(request);
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
                                String enddate = response.body().getString("data");
                                String id = response.body().getString("id");
                                SharedData.setIsSubscribed(context, "true");
                                SharedData.setPlanId(context, id);
                                SharedData.setPlanEndDate(context, enddate);
                                //Toast.makeText(context, "Subscription successful", Toast.LENGTH_SHORT).show();
                                if(context.getClass()==PremiumPlansctivity.class)
                                    context.finish();
                            } catch (Exception ex) {

                            }

                        } else if (response.code() == 500) {
                            if (response.errorBody() != null) {
                                //Toast.makeText(context, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(context, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    } catch (Exception ex) {
                        progressDialog.dismiss();
                        //Toast.makeText(context, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    // hideProgress();
                    progressDialog.dismiss();
                   // Toast.makeText(context, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception ex) {
            Utility.hideProgress(progressDialog);
        }


    }

}
