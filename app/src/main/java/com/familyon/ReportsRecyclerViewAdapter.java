package com.familyon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.recyclerview.widget.RecyclerView;

import com.example.wstatsapp.R;

import java.util.concurrent.TimeUnit;


public class ReportsRecyclerViewAdapter extends RecyclerView.Adapter<ReportsRecyclerViewAdapter.ViewHolder> {

    private final JSONArray mValues;
    //private final OnListFragmentInteractionListener mListener;
    Context ctx;
    String mType;

    public ReportsRecyclerViewAdapter(JSONArray items, String type) {
        mValues = items;
        mType = type;
        // mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(mType.equals("now"))
        {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.current_status, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.report_item, parent, false);
        }
        ctx = parent.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            JSONObject jsonObject = mValues.getJSONObject(position);
            if (mType.equals("now")) {
                if (jsonObject.has("offline_date")) {
                    holder.status_icon.setImageResource(R.drawable.red_circle);
                    holder.status.setText("OFFLINE");
                } else {
                    holder.status_icon.setImageResource(R.drawable.green_circle);
                    holder.status.setText("ONLINE");
                }
            } else {
                if (jsonObject.has("offline_date")) {
                    String datetime = Utility.ConvertMillisTodateString(jsonObject.getString("offline_time"));
                    holder.offlinedate.setText(datetime.split("_")[0]);
                    holder.offlinetime.setText(datetime.split("_")[1]);
                }
                if (jsonObject.has("online_date")) {
                    String datetime = Utility.ConvertMillisTodateString(jsonObject.getString("online_time"));
                    holder.onlinedate.setText(datetime.split("_")[0]);
                    holder.onlinetime.setText(datetime.split("_")[1]);
                }

                if (jsonObject.has("duration")) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(jsonObject.getLong("duration"));
                    if (minutes > 0)
                        holder.duration.setText(minutes + " mins");
                    else
                        holder.duration.setText(TimeUnit.MILLISECONDS.toSeconds(jsonObject.getLong("duration")) + " secs");
                }
            }


        } catch (Exception ex) {

        }

    }


    @Override
    public int getItemCount() {
        return mValues.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        //public final ImageView moviePoster;
        public final TextView onlinetime, onlinedate, offlinetime, offlinedate, duration, status;
        public ImageView status_icon;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            onlinetime = (TextView) view.findViewById(R.id.onlinetime);
            onlinedate = view.findViewById(R.id.onlinedate);
            offlinetime = view.findViewById(R.id.offlinetime);
            offlinedate = view.findViewById(R.id.offlinedate);
            duration = view.findViewById(R.id.duration);
            status = view.findViewById(R.id.status);
            status_icon = view.findViewById(R.id.status_icon);
        }

    }
}
