package com.familyon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wstatsapp.R;
import com.familyon.SPHelpher.SessionManager;
import com.familyon.SPHelpher.SharedData;
import com.familyon.server.ApiClient;
import com.familyon.server.ApiInterface;
import com.google.gson.Gson;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.familyon.server.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class AddNumberFragment extends Fragment {

    static EditText contactName, contactNumber;
    static LinearLayout saveContact, getPremium;
    static ProgressDialog progressDialog;
    static CountryCodePicker ccp;
    static TextView text_status, text_timer;
    static SessionManager sessionManager;
    static CountDownTimer cTimer = null;
    static String timer, isPaid;
    static ImageButton pickContact;
    static final int RESULT_PICK_CONTACT = 121;
    static CardView subcribePanel;
    private static long timer_ms = 28800000;
    private static long remainingTime = 0;
    public static int i = 0;
    static Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_number, container, false);
        activity=getActivity();
        getPremium = view.findViewById(R.id.getPremium);
        contactName = view.findViewById(R.id.contactName);
        contactNumber = view.findViewById(R.id.contactNumber);
        saveContact = view.findViewById(R.id.saveContact);
        ccp = view.findViewById(R.id.ccp);
        text_status = view.findViewById(R.id.text_status);
        text_timer = view.findViewById(R.id.text_timer);

        sessionManager = new SessionManager(activity);
        pickContact = view.findViewById(R.id.pickContact);
        subcribePanel = view.findViewById(R.id.subcribePanel);
        HashMap<String, String> user = sessionManager.getUserDetails();
        timer = user.get(SessionManager.KEY_TIMER);
       // System.out.println(timer);


        isPaid="IsPaid";
        if (isPaid.equalsIgnoreCase("IsPaid")){
            text_timer.setVisibility(View.GONE);
            text_status.setText("Start Tracking");
        }


        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isPaid.equalsIgnoreCase("IsPaid")){
                    if ((!SharedData.getIsSubscribed(activity)) || SharedData.getIsPlanExpired(activity)) {
                        ShowPremiumDialog();

                    } else if (SharedData.getIsTracking(activity)) {
                        text_status.setText("START TRACKING");
                        SharedData.setTrackerStoppedAt(activity, System.currentTimeMillis());
                        SharedData.setIsTracking(activity, false);
                        if (cTimer != null) {
                            cTimer.cancel();
                        }
                        saveContact.setBackgroundResource(R.color.colorAccent);
                        ChangeTrackingStatus();
                        contactName.setEnabled(true);
                        contactNumber.setEnabled(true);
                        ccp.setEnabled(true);
                        pickContact.setEnabled(true);
                    } else {
                        if (TextUtils.isEmpty(contactName.getText())) {
                            Toast.makeText(activity, "Name is required", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(contactNumber.getText())) {
                            Toast.makeText(activity, "Number is required", Toast.LENGTH_SHORT).show();
                        } else {
                            ShowAlert();
                        }
                    }

                }

                else {
                    if ((SharedData.getIsTrialExpired(activity) && !SharedData.getIsSubscribed(activity)) || SharedData.getIsPlanExpired(activity)) {
                        ShowPremiumDialog();

                    } else if (SharedData.getIsTracking(activity)) {
                        text_status.setText("START TRACKING");
                        SharedData.setTrackerStoppedAt(activity, System.currentTimeMillis());
                        SharedData.setIsTracking(activity, false);
                        if (cTimer != null) {
                            cTimer.cancel();
                        }
                        saveContact.setBackgroundResource(R.color.colorAccent);
                        ChangeTrackingStatus();
                        contactName.setEnabled(true);
                        contactNumber.setEnabled(true);
                        ccp.setEnabled(true);
                        pickContact.setEnabled(true);
                    } else {
                        if (TextUtils.isEmpty(contactName.getText())) {
                            Toast.makeText(activity, "Name is required", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(contactNumber.getText())) {
                            Toast.makeText(activity, "Number is required", Toast.LENGTH_SHORT).show();
                        } else {
                            ShowAlert();
                        }
                    }
                }
            }
        });

        getPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, PremiumPlansctivity.class));
            }
        });

        contactName.setText(SharedData.getTrackingName(activity));
        String number = SharedData.getTrackingNumber(activity);
        if (!TextUtils.isEmpty(number)) {
            contactNumber.setText(number);
            // ccp.setCountryForPhoneCode(Integer.parseInt(number.substring(0, number.length() - 10)));
        }
//        else {
//            ccp.resetToDefaultCountry();
//        }

        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });

        return view;

    }


    public static void SaveContact() {
        progressDialog = Utility.showProgress(activity);
        ApiInterface apiService;
        final int userCount = SharedData.getUserCount(activity);
//        if (userCount >0) {
//            apiService = ApiClient.getClient(activity).create(ApiInterface.class);
//        } else {
        apiService = ApiClient.getClient(activity).create(ApiInterface.class);
        //}
        // JSONObject model = new JSONObject();


        try {

            HashMap<String, String> request = new HashMap<>();
            request.put("user_id", SharedData.getUserID(activity));
            request.put("name", contactName.getText().toString());
            request.put("mobile_number", ccp.getSelectedCountryCode() + contactNumber.getText().toString());

            Call<JSONObject> call = apiService.SaveContacts(request);
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
                                SubscribeOnYowsup(response.body().getString("ip"));
                                //text_status.setText(R.string.stop_tracking);
                                // saveContact.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                SharedData.setIsTracking(activity, true);
                                if (!SharedData.getIsSubscribed(activity)) {
                                    long trackerStartedAt = SharedData.getTrackerStartedAt(activity);
                                    if (trackerStartedAt == 0) {
                                        SharedData.setTrackerStartedAt(activity, System.currentTimeMillis());
                                    }
                                    if (!isPaid.equalsIgnoreCase("IsPaid")){
                                        startTimer();
                                    }
                                } else {
                                    text_status.setText("STOP TRACKING");
                                    saveContact.setBackgroundResource(R.color.colorPrimary);
                                }
                                Toast.makeText(activity, "Tracking Started!", Toast.LENGTH_SHORT).show();
                                //onBackPressed();
                            } catch (Exception ex) {

                            }

                        } else if (response.code() == 500) {

                            if (response.errorBody() != null) {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    } catch (Exception ex) {

                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    // hideProgress();
                    try {
                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }


            });
        } catch (Exception ex) {
            Utility.hideProgress(progressDialog);
        }
    }

    public static void startTimer() {
        if (SharedData.getIsTracking(activity)) {
            contactName.setEnabled(false);
            contactNumber.setEnabled(false);
            ccp.setEnabled(false);
            pickContact.setEnabled(false);
            if (cTimer != null) {
                cTimer.cancel();
            }
            if (SharedData.getIsSubscribed(activity)) {
                timer_ms = Utility.convertDateToMillis(SharedData.getPlanEndDate(activity));
                remainingTime = timer_ms - System.currentTimeMillis();
            } else if (SharedData.getTrackerStoppedAt(activity) > 0 || SharedData.getTrackerStoppedDuration(activity) > 0) {
                long duration = 0;
                long previousDuration = SharedData.getTrackerStoppedDuration(activity);
                if (SharedData.getTrackerStoppedAt(activity) > 0) {
                    duration = System.currentTimeMillis() - SharedData.getTrackerStoppedAt(activity);
                    SharedData.setTrackerStoppedDuration(activity, previousDuration + duration);
                    SharedData.setTrackerStoppedAt(activity, 0);
                }
                timer_ms = SharedData.GetTrialHours(SharedData.getTrackerStartedAt(activity));
                remainingTime = (timer_ms + previousDuration + duration) - System.currentTimeMillis();

            } else {
                timer_ms = SharedData.GetTrialHours(SharedData.getTrackerStartedAt(activity));
                remainingTime = timer_ms - System.currentTimeMillis();
            }


            if (remainingTime <= 0) {
                text_status.setText("Trial Expired");
                saveContact.setBackgroundResource(R.color.colorGray);

                text_timer.setVisibility(View.GONE);
                SharedData.setIsTracking(activity, false);
                SharedData.setTrialExpired(activity, "true");
                UpdateTracking();

            } else {
                cTimer = new CountDownTimer(remainingTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        // t2.setText("seconds remaining: " + millisUntilFinished / 1000);
                        long millis = millisUntilFinished;
                        SharedData.setTimeRemaining(activity, millis);
                        String hms = String.format("%02d:%02d:%02d",
                                //Hours
                                TimeUnit.MILLISECONDS.toHours(millis) -
                                        TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
                                //Minutes
                                TimeUnit.MILLISECONDS.toMinutes(millis) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                                //Seconds
                                TimeUnit.MILLISECONDS.toSeconds(millis) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

                        text_timer.setText(hms);//set text
                        text_status.setText(R.string.stop_tracking);
                        if (hms.matches("00:00:01")) {
                            ShowAlerts();
                        }
                        sessionManager.createSession(text_timer.getText().toString());
                        HashMap<String, String> user = sessionManager.getUserDetails();
                        String timer = user.get(SessionManager.KEY_TIMER);
                        System.out.println(timer);
                        saveContact.setBackgroundResource(R.color.colorPrimary);

                    }

                    public void onFinish() {
                        //sendotp.setBackgroundResource(R.drawable.shapelogin);
                        // sendotp.setClickable(true);
                        //sendotp.setText(getString(R.string.resend_otp));
                        text_status.setText("Trial Expired");
                        saveContact.setBackgroundResource(R.color.colorGray);

                        text_timer.setVisibility(View.GONE);
                        SharedData.setIsTracking(activity, false);
                        SharedData.setTrialExpired(activity, "true");
                        UpdateTracking();
                    }
                };
                cTimer.start();
            }
        } else if (SharedData.getIsTrialExpired(activity)) {
            text_status.setText("Trial Expired");
            saveContact.setBackgroundResource(R.color.colorGray);

            text_timer.setVisibility(View.GONE);
        } else if (SharedData.getTrackerStoppedAt(activity) > 0 && SharedData.getTimeRemaining(activity) > 0) {
            text_status.setText("START TRACKING");
            long millis = SharedData.getTimeRemaining(activity);
            String hms = String.format("%02d:%02d:%02d",
                    //Hours
                    TimeUnit.MILLISECONDS.toHours(millis) -
                            TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)),
                    //Minutes
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    //Seconds
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            text_timer.setText(hms);//set text
            if (hms.matches("00:00:01")) {
                ShowAlerts();
            }
            text_timer.setVisibility(View.VISIBLE);
        } else if (SharedData.getIsTrialExpired(activity)) {
            text_status.setText("Trial Expired");
            saveContact.setBackgroundResource(R.color.colorGray);

            text_timer.setVisibility(View.GONE);
            SharedData.setIsTracking(activity, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        manageSubscriptionPanel();
    }

    public static void manageSubscriptionPanel() {
        if (isPaid.equalsIgnoreCase("IsPaid")){

            if (SharedData.getIsSubscribed(activity)) {
                pickContact.setVisibility(View.VISIBLE);
                subcribePanel.setVisibility(View.GONE);
                if (SharedData.getIsTracking(activity)) {
                    text_status.setText("STOP TRACKING");
                    saveContact.setBackgroundResource(R.color.colorPrimary);
                    contactName.setEnabled(false);
                    contactNumber.setEnabled(false);
                    ccp.setEnabled(false);
                    pickContact.setEnabled(false);
                } else {
                    text_status.setText("START TRACKING");
                    saveContact.setBackgroundResource(R.color.colorAccent);
                    contactName.setEnabled(true);
                    contactNumber.setEnabled(true);
                    ccp.setEnabled(true);
                    pickContact.setEnabled(true);
                }
            }

        }
        else{
            if (!SharedData.getIsSubscribed(activity)) {
                startTimer();
            } else {
                pickContact.setVisibility(View.VISIBLE);
                subcribePanel.setVisibility(View.GONE);
                if (SharedData.getIsSubscribed(activity)) {
                    timer_ms = Utility.convertDateToMillis(SharedData.getPlanEndDate(activity));
                    remainingTime = timer_ms - System.currentTimeMillis();
                    if (remainingTime > 0) {

                        if (SharedData.getIsTracking(activity)) {
                            text_status.setText("STOP TRACKING");
                            saveContact.setBackgroundResource(R.color.colorPrimary);
                            contactName.setEnabled(false);
                            contactNumber.setEnabled(false);
                            ccp.setEnabled(false);
                            pickContact.setEnabled(false);
                        } else {
                            text_status.setText("START TRACKING");
                            saveContact.setBackgroundResource(R.color.colorAccent);
                            contactName.setEnabled(true);
                            contactNumber.setEnabled(true);
                            ccp.setEnabled(true);
                            pickContact.setEnabled(true);
                        }
                    } else {
                        SharedData.setPlanExpired(activity, "true");
                        text_status.setText("Plan Expired");
                        saveContact.setBackgroundResource(R.color.colorGray);
                        SharedData.setIsTracking(activity, false);
                    }
                }
                text_timer.setVisibility(View.GONE);
            }
        }
    }

    public static void ShowAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.confirmation_dialog, null);
        dialog.setView(customView);

        TextView ctNumber = (TextView) customView.findViewById(R.id.contactNumber);
        ctNumber.setText(ccp.getSelectedCountryCodeWithPlus() + " " + contactNumber.getText().toString());
        LinearLayout confirm = customView.findViewById(R.id.confirm_button);
        LinearLayout edit = customView.findViewById(R.id.editNumber);
        final AlertDialog alertDialogAndroid = dialog.create();
        alertDialogAndroid.setCanceledOnTouchOutside(false);
        alertDialogAndroid.setCancelable(false);
        dialog.setCancelable(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
                SharedData.setTrackingName(activity, contactName.getText().toString());
                SharedData.setTrackingNumber(activity, contactNumber.getText().toString());
                SharedData.setTrackingCC(activity, ccp.getSelectedCountryNameCode().toString());
                contactName.setEnabled(false);
                contactNumber.setEnabled(false);
                ccp.setEnabled(false);
                pickContact.setEnabled(false);
                SaveContact();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
            }
        });
        alertDialogAndroid.show();
    }

    public static void ShowAlerts() {
        i = 1;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.dialog_two, null);
        dialog.setView(customView);

        TextView ctNumber = (TextView) customView.findViewById(R.id.contactNumber);
        LinearLayout confirm = customView.findViewById(R.id.confirm_button);

        final AlertDialog alertDialogAndroid = dialog.create();
        alertDialogAndroid.setCanceledOnTouchOutside(false);
        alertDialogAndroid.setCancelable(false);
        dialog.setCancelable(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
            }
        });

        alertDialogAndroid.show();
    }


    public void ShowPremiumDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        View customView = inflater.inflate(R.layout.subscription_dialog, null);
        dialog.setView(customView);

        LinearLayout confirm = customView.findViewById(R.id.getPremium);

        if (isPaid.equalsIgnoreCase("IsPaid")){
            TextView tvSubText= customView.findViewById(R.id.tvSubText);
            tvSubText.setText("Please get a premium plan to track any WhatsApp number");
        }


        final AlertDialog alertDialogAndroid = dialog.create();
        alertDialogAndroid.setCanceledOnTouchOutside(false);
        alertDialogAndroid.setCancelable(false);
        dialog.setCancelable(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAndroid.dismiss();
                startActivity(new Intent(activity, PremiumPlansctivity.class));
            }
        });
        alertDialogAndroid.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Cursor cursor = null;
                    try {
                        String phoneNo = null;
                        String name = null;

                        Uri uri = data.getData();
                        cursor = activity.getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                        phoneNo = cursor.getString(phoneIndex).trim().replaceAll(" ", "").replaceAll("-", "");
                        name = cursor.getString(nameIndex);
                        String countryCode = "";
                        contactName.setText(name);
//                        if (phoneNo.length() > 10) {
//                            contactNumber.setText(phoneNo.substring(phoneNo.length() - 10, phoneNo.length()));
//                            countryCode = phoneNo.substring(0, phoneNo.length() - 10);
//                        }
//                        else
//                        {
                        contactNumber.setText(phoneNo);
//                        }

                        //countryCode = countryCode.replace("+", "");
                        //ccp.setCountryForPhoneCode(Integer.parseInt(countryCode));
                        //Log.e("Name and Contact number is",name+","+phoneNo);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("Failed", "Not able to pick contact");
        }
    }


    public static void UpdateTracking() {
        progressDialog = Utility.showProgress(activity);
        ApiInterface apiService;
        final int userCount = SharedData.getUserCount(activity);
//        if (userCount >0) {
//            apiService = ApiClient.getClient(activity).create(ApiInterface.class);
//        } else {
        apiService = ApiClient.getClient(activity).create(ApiInterface.class);
        //}
        // JSONObject model = new JSONObject();


        try {

            HashMap<String, String> request = new HashMap<>();
            request.put("user_id", SharedData.getUserID(activity));
            //request.put("end", String.valueOf(System.currentTimeMillis()));

            Call<JSONObject> call = apiService.UpdateTracking(request);
            // showProgress();
            call.enqueue(new Callback<JSONObject>() {

                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    // hideProgress();
                    try {
                        Utility.hideProgress(progressDialog);
                        if (response.code() == 200) {
                            Gson gson = new Gson();
                            try {

                                //text_status.setText(R.string.stop_tracking);
                                // saveContact.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                SharedData.setIsTracking(activity, false);


                                // Toast.makeText(activity, "Contact saved", Toast.LENGTH_SHORT).show();
                                //onBackPressed();
                            } catch (Exception ex) {

                            }

                        } else if (response.code() == 500) {

                            if (response.errorBody() != null) {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    } catch (Exception ex) {

                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    // hideProgress();
                    try {
                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }


            });
        } catch (Exception ex) {
            Utility.hideProgress(progressDialog);
        }
    }

    public void ChangeTrackingStatus() {
        progressDialog = Utility.showProgress(activity);
        ApiInterface apiService;
        final int userCount = SharedData.getUserCount(activity);
//        if (userCount >0) {
//            apiService = ApiClient.getClient(activity).create(ApiInterface.class);
//        } else {
        apiService = ApiClient.getClient(activity).create(ApiInterface.class);
        //}
        // JSONObject model = new JSONObject();


        try {

            HashMap<String, String> request = new HashMap<>();
            request.put("user_id", SharedData.getUserID(activity));
            request.put("status", "1");

            Call<JSONObject> call = apiService.ChangeTrackingStatus(request);
            // showProgress();
            call.enqueue(new Callback<JSONObject>() {

                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    // hideProgress();
                    try {
                        Utility.hideProgress(progressDialog);
                        if (response.code() == 200) {
                            Gson gson = new Gson();
                            try {

                                //text_status.setText(R.string.stop_tracking);
                                // saveContact.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                SharedData.setIsTracking(activity, false);
                                SharedData.setTrackerStoppedAt(activity, System.currentTimeMillis());

                                Toast.makeText(activity, "Tracking stopped", Toast.LENGTH_SHORT).show();
                                //onBackPressed();
                            } catch (Exception ex) {

                            }

                        } else if (response.code() == 500) {

                            if (response.errorBody() != null) {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    } catch (Exception ex) {

                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }


                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    // hideProgress();
                    try {
                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                }


            });
        } catch (Exception ex) {
            Utility.hideProgress(progressDialog);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cTimer != null) {
            cTimer.cancel();
        }
    }

    public static void SubscribeOnYowsup(String IP) {
        progressDialog = Utility.showProgress(activity);
        ApiInterface apiService;
        final int userCount = SharedData.getUserCount(activity);
//        if (userCount >0) {
//            apiService = ApiClient.getClient(activity).create(ApiInterface.class);
//        } else {
        apiService = ApiClient.getClient1(activity).create(ApiInterface.class);

        //}
        // JSONObject model = new JSONObject();


        try {

            HashMap<String, String> request = new HashMap<>();
            request.put("mobile_number", ccp.getSelectedCountryCode() + contactNumber.getText().toString());

            Call<JSONObject> call = apiService.SubscribeUser("http://" + IP + Constants.SubscribeYowsup, request);
            // showProgress();
            call.enqueue(new Callback<JSONObject>() {

                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    // hideProgress();
                    try {
                        Utility.hideProgress(progressDialog);
                        if (response.code() == 200) {


                        } else if (response.code() == 500) {

                            if (response.errorBody() != null) {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                            }

                        } else {

                        }
                    } catch (Exception ex) {

                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }


                }


                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    // hideProgress();
                    try {
                        progressDialog.dismiss();
                        Toast.makeText(activity, R.string.internal_server_error, Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }


            });
        } catch (Exception ex) {
            Utility.hideProgress(progressDialog);
        }
    }


}
