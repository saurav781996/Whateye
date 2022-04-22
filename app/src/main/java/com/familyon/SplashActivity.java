package com.familyon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.wstatsapp.R;
import com.familyon.SPHelpher.SharedData;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private String TAG = "SplashActivity";
    private boolean isRemoveAds = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        isRemoveAds = SharedData.getIsSubscribed(this);

        final SdkConfiguration.Builder configBuilder = new SdkConfiguration.Builder(getString(R.string.mopub_banner));
        SdkConfiguration sdkConfiguration = configBuilder.build();

        MoPub.initializeSdk(this, sdkConfiguration, new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                Log.e("MoPub", "init");
            }
        });

        requestInterstitial();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            showInterstitialAdBeforeLogin();
                    }
                });


            }
        }, 12_000);
    }



private MoPubInterstitial mInterstitial;

public void requestInterstitial() {
    Log.e("MoPub", "requestInterstitial");
    mInterstitial = new MoPubInterstitial(this, getString(R.string.mopub_splash_interstitial));
    mInterstitial.setInterstitialAdListener(interstitialAdListener);
    mInterstitial.load();

}

public void showInterstitialAdBeforeLogin() {
    Log.e(TAG, "Trying to show interstitial " + mInterstitial.isReady());
    if (mInterstitial != null && mInterstitial.isReady() && !isRemoveAds) {
        mInterstitial.show();
    } else {
        launchHomeActivity();
    }
}


int noOfTry = 1;

private MoPubInterstitial.InterstitialAdListener interstitialAdListener = new MoPubInterstitial.InterstitialAdListener() {
    @Override
    public void onInterstitialLoaded(MoPubInterstitial moPubInterstitial) {
        Log.e("MoPub", "Loaded");
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial moPubInterstitial, MoPubErrorCode moPubErrorCode) {
        Log.e("MoPub", "onInterstitialFailed " + moPubErrorCode.toString());
        if (noOfTry > 0) {
            requestInterstitial();
        }
        --noOfTry;
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial moPubInterstitial) {

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial moPubInterstitial) {

    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial moPubInterstitial) {
        launchHomeActivity();
    }
};


private void launchHomeActivity() {
    Intent intent =new Intent(SplashActivity.this, HomeActivity.class);
    intent.putExtra("NoAddSplash","NoAddSplash");
    startActivity(intent);
    finish();
}

@Override
protected void onDestroy() {

    if (mInterstitial != null) {
        mInterstitial.destroy();
    }

    super.onDestroy();
}




}
