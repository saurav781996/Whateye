package com.familyon.server;


import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface ApiInterface {

    @FormUrlEncoded
    @POST(Constants.GetContacts)
    Call<JSONObject> GetContacts(@FieldMap HashMap<String, String> params);


    @FormUrlEncoded
    @POST(Constants.GetReports)
    Call<JSONObject> GetReports(@FieldMap HashMap<String, String> params);


    @FormUrlEncoded
    @POST(Constants.SubscribeUser)
    Call<JSONObject> SaveContacts(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST(Constants.CreateUser)
    Call<JSONObject> CreateUser(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST(Constants.GetPlans)
    Call<JSONObject> GetPlans(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST(Constants.SuccessPayment)
    Call<JSONObject> SuccessPayment(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST(Constants.ErrorPayment)
    Call<JSONObject> ErrorPayment(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST(Constants.ClearReports)
    Call<JSONObject> ClearReports(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST(Constants.UpdateTracking)
    Call<JSONObject> UpdateTracking(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST(Constants.ChangeTracking)
    Call<JSONObject> ChangeTrackingStatus(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST
    Call<JSONObject> SubscribeUser(@Url String url,@FieldMap HashMap<String, String> params);

}

