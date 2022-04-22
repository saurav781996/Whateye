package com.familyon.server;

import android.content.Context;

import com.example.wstatsapp.BuildConfig;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;


public class ApiClient {
    private static Retrofit retrofit = null;
    private static Retrofit retrofit1 = null;
   // public static final String BASE_URL = "http://38.17.52.102:5000/";
    public static final String BASE_URL = "http://52.206.49.241/appentus/index.php/Api/";

    public static Retrofit getClient(final Context context) {
        if (retrofit == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            //.header("x-auth-token", AppPreferences.getToken(context))
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Password", "123456")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            OkHttpClient client = null;
            if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                client = httpClient
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        //.addInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .build();

            } else {
                client = httpClient
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();

            }
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(JSONConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static Retrofit getClient1(final Context context) {
        if (retrofit1 == null) {

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            //.header("x-auth-token", AppPreferences.getToken(context))
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header("Password", "123456")
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            OkHttpClient client = null;
            if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                client = httpClient
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        //.addInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
                        .build();

            } else {
                client = httpClient
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(50, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();

            }
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            retrofit1 = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(JSONConverterFactory.create())
                    .build();
        }
        return retrofit1;
    }


}
