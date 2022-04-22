package com.familyon;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


public class TrackingAdapter extends FragmentStateAdapter {
    private Context myContext;
    int totalTabs;

    Bundle bundle;

    public TrackingAdapter(FragmentActivity context, int totalTabs) {
        super(context);

        myContext = context;
        this.totalTabs = totalTabs;
        //notifyDataSetChanged();
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:

                AddNumberFragment addNumberFragment = new AddNumberFragment();
                bundle = new Bundle();
                //bundle.putString("status", "Scheduled");
                addNumberFragment.setArguments(bundle);
                return addNumberFragment;
            case 1:

                TrackingFragment2 trackingFragment = new TrackingFragment2();
                bundle = new Bundle();
               // bundle.putString("status", "Accepted");
                trackingFragment.setArguments(bundle);
                return trackingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}
