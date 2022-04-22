package com.familyon;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.example.wstatsapp.R;

import java.util.List;

public class PlansRecyclerViewAdapter extends RecyclerView.Adapter<PlansRecyclerViewAdapter.ViewHolder> {

    private final List<SkuDetails> skuDetailsList;
    //private final OnListFragmentInteractionListener mListener;
    Activity ctx;
    ProgressDialog progressDialog;
    BillingClient billingClient;

    public PlansRecyclerViewAdapter(List<SkuDetails> skuDetailsList, Activity context, BillingClient client) {
        this.skuDetailsList = skuDetailsList;
        ctx = context;
        billingClient = client;
        // mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.plan_item, parent, false);
        //ctx = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            SkuDetails jsonObject = skuDetailsList.get(position);
            holder.mValue = jsonObject;
            switch (position) {
                case 0:
                    holder.bg.setBackgroundResource((R.drawable.weekly_bg));
                    holder.planName.setText("WEEKLY PREMIUM");
                    holder.planDescription.setText("per week");
                    holder.planFequency.setText("Track Any WhatsApp Number!");
                    break;
                case 1:
                    holder.bg.setBackgroundResource((R.drawable.monthly_bg));
                    holder.planName.setText("MONTHLY PREMIUM");
                    holder.planDescription.setText("per month");
                    holder.planFequency.setText("Track Any WhatsApp Number!");
                    break;
                case 2:
                    holder.bg.setBackgroundResource((R.drawable.annual_bg));
                    holder.planName.setText("3 MONTH PREMIUM");
                    holder.planDescription.setText("per 3 month");
                    holder.planFequency.setText("Track Any WhatsApp Number!");
                    holder.bestvalue.setVisibility(View.VISIBLE);
                    break;
            }

            holder.planAmount.setText(jsonObject.getPrice());



            holder.getPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetailsList.get(position))
                                .build();
                        int responseCode = billingClient.launchBillingFlow(ctx, billingFlowParams).getResponseCode();
                        // SavePayment("weekly_plans_07");

                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            });

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final ImageView moviePoster;
        public final TextView planName, planAmount, planFequency, planDescription;
        LinearLayout bg;
        SkuDetails mValue;
        Button getPlan;
        RelativeLayout bestvalue;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            planName = (TextView) view.findViewById(R.id.planName);
            planAmount = view.findViewById(R.id.planAmount);
            planFequency = view.findViewById(R.id.planFrequency);
            planDescription = view.findViewById(R.id.planDetail);
            bg = view.findViewById(R.id.bg);
            getPlan = view.findViewById(R.id.getPlan);
            bestvalue=view.findViewById(R.id.bestvalue);
        }

    }


}
