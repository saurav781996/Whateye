package com.familyon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.wstatsapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PremiumPlansctivity extends AppCompatActivity  {
    ProgressDialog progressDialog;
    RecyclerView plansRecycler;
    public PurchasesUpdatedListener purchaseUpdateListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Toast.makeText(PremiumPlansctivity.this, "Last transaction was cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // Handle any other error codes.
                Toast.makeText(PremiumPlansctivity.this, "An error occured, while processing your request.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_plansctivity);
        plansRecycler = findViewById(R.id.plansRecycler);
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Premium Plans");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // back button pressed
                onBackPressed();
            }
        });
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    if(Utility.skuDetails==null || Utility.skuDetails.size()==0) {
                        // The BillingClient is ready. You can query purchases here.
                        List<String> skuList = new ArrayList<>();
                        skuList.add("weekly_plans_07");
                        skuList.add("monthly_plan_30");
                        skuList.add("three_month_plan_90");

                        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

                        billingClient.querySkuDetailsAsync(params.build(),
                                new SkuDetailsResponseListener() {
                                    @Override
                                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                                     List<SkuDetails> skuDetailsList) {

                                        try {
                                            if (skuDetailsList!=null){
                                                // Process the result.
                                                Collections.sort(skuDetailsList, new Comparator<SkuDetails>() {
                                                    public int compare(SkuDetails obj1, SkuDetails obj2) {
                                                        // ## Ascending order
                                                        //return obj1.getPrice().compareToIgnoreCase(obj2.getPrice()); // To compare string values
                                                        return Double.valueOf(obj1.getPriceAmountMicros()).compareTo(Double.valueOf(obj2.getPriceAmountMicros())); // To compare integer values

                                                        // ## Descending order
                                                        // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                                        // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                                                    }
                                                });
                                                if (skuDetailsList.size() > 0) {
                                                    plansRecycler.setLayoutManager(new LinearLayoutManager(PremiumPlansctivity.this));
                                                    plansRecycler.setAdapter(new PlansRecyclerViewAdapter(skuDetailsList, PremiumPlansctivity.this, billingClient));
                                                } else {
                                                    Utility.ShowAlert(PremiumPlansctivity.this, "No Plans Found");
                                                }
                                            }
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                    }
                    else
                    {
                        Collections.sort(Utility.skuDetails, new Comparator<SkuDetails>() {
                            public int compare(SkuDetails obj1, SkuDetails obj2) {
                                // ## Ascending order
                                //return obj1.getPrice().compareToIgnoreCase(obj2.getPrice()); // To compare string values
                                return Double.valueOf(obj1.getPriceAmountMicros()).compareTo(Double.valueOf(obj2.getPriceAmountMicros())); // To compare integer values

                                // ## Descending order
                                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                // return Integer.valueOf(obj2.empId).compareTo(Integer.valueOf(obj1.empId)); // To compare integer values
                            }
                        });
                        plansRecycler.setLayoutManager(new LinearLayoutManager(PremiumPlansctivity.this));
                        plansRecycler.setAdapter(new PlansRecyclerViewAdapter(Utility.skuDetails, PremiumPlansctivity.this, billingClient));
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
        //GetPlans();
    }





    void handlePurchase(final Purchase purchase) {


        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
                    @Override
                    public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            // Handle the success of the consume operation.
                            Utility.SavePayment(purchase.getSku(),PremiumPlansctivity.this);
                        }
                    }
                });
            }
        }
        else
        {
            Toast.makeText(this, "Your transaction is under progress, we will notify you once it is completed.", Toast.LENGTH_SHORT).show();
        }


//        ConsumeParams consumeParams =
//                ConsumeParams.newBuilder()
//                        .setPurchaseToken(purchase.getPurchaseToken())
//                        .build();
//
//        ConsumeResponseListener listener = new ConsumeResponseListener() {
//            @Override
//            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
//                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//                    // Handle the success of the consume operation.
//                    Utility.SavePayment(purchase.getSku(),PremiumPlansctivity.this);
//                }
//            }
//        };

        //billingClient.consumeAsync(consumeParams, listener);
    }


}
