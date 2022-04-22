package com.familyon.SPHelpher;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;


public class SharedData {

    public static SharedPreferences getSP(Context context)
    {
        return context.getSharedPreferences(Name_Struct.Shared_Prefrence_Name, Context.MODE_PRIVATE);
    }

    public static void setUserID(Context context,String UserId) {

        getSP(context).edit().putString(Name_Struct.UserID,UserId).commit();

    }
    public static String getUserID(Context context) {

        return getSP(context).getString(Name_Struct.UserID, "");
    }

    public static int getUserCount(Context context) {

        return getSP(context).getInt(Name_Struct.UserCount, 28);
    }

    public static void setUserCount(Context context,int count) {

        getSP(context).edit().putInt(Name_Struct.UserCount,count).commit();

    }

    public static void setDeviceID(Context context,String UserId) {

        getSP(context).edit().putString(Name_Struct.DeviceID,UserId).commit();

    }
    public static String getDeviceID(Context context) {

        return getSP(context).getString(Name_Struct.DeviceID, "");
    }
    public static void setToken(Context context,String UserId) {

        getSP(context).edit().putString(Name_Struct.Token,UserId).commit();

    }
    public static String getToken(Context context) {

        return getSP(context).getString(Name_Struct.Token, "");
    }

    public static void setOnlineNotification(Context context,String online) {

        getSP(context).edit().putString(Name_Struct.OnlineNotification,online).commit();

    }
    public static boolean getOnlineNotification(Context context) {

        return Boolean.parseBoolean(getSP(context).getString(Name_Struct.OnlineNotification, "true"));
    }
    public static void setOfflineNotification(Context context,String offline) {

        getSP(context).edit().putString(Name_Struct.OfflineNotification,offline).commit();

    }
    public static boolean getOfflineNotification(Context context) {

        return Boolean.parseBoolean(getSP(context).getString(Name_Struct.OfflineNotification, "true"));
    }
    public static void setNotificationSound(Context context,String sound) {

        getSP(context).edit().putString(Name_Struct.NotificationSound,sound).commit();

    }
    public static boolean getNotificationSound(Context context) {

        return Boolean.parseBoolean(getSP(context).getString(Name_Struct.NotificationSound, "true"));
    }

    public static void setNotificationVibrate(Context context,String sound) {

        getSP(context).edit().putString(Name_Struct.NotificationVibrate,sound).commit();

    }
    public static boolean getNotificationVibrate(Context context) {

        return Boolean.parseBoolean(getSP(context).getString(Name_Struct.NotificationVibrate, "true"));
    }

    public static void setPlanId(Context context,String planId) {

        getSP(context).edit().putString(Name_Struct.PlanId,planId).commit();

    }
    public static String getPlanId(Context context) {

        return getSP(context).getString(Name_Struct.PlanId, "0");
    }
    public static void setPlanName(Context context,String planName) {

        getSP(context).edit().putString(Name_Struct.PlanName,planName).commit();

    }
    public static String getPlanName(Context context) {

        return getSP(context).getString(Name_Struct.PlanName, "Free");
    }
    public static void setPlanDays(Context context,String days) {

        getSP(context).edit().putString(Name_Struct.PlanDays,days).commit();

    }
    public static String getPlanDays(Context context) {

        return getSP(context).getString(Name_Struct.PlanDays, "0");
    }

    public static void setPlanEndDate(Context context,String days) {

        getSP(context).edit().putString(Name_Struct.PlanEndDate,days).commit();

    }
    public static String getPlanEndDate(Context context) {

        return getSP(context).getString(Name_Struct.PlanEndDate, "0");
    }


    public static void setPlanStartDate(Context context,String days) {

        getSP(context).edit().putString(Name_Struct.PlanStartDate,days).commit();

    }
    public static String getPlanStartDate(Context context) {

        return getSP(context).getString(Name_Struct.PlanStartDate, "0");
    }




    public static void setIsSubscribed(Context context,String subscribed) {

        getSP(context).edit().putString(Name_Struct.IsSubscribed,subscribed).commit();

    }
    public static boolean getIsSubscribed(Context context) {

        return Boolean.parseBoolean(getSP(context).getString(Name_Struct.IsSubscribed, "false"));
    }

    public static void setTrialExpired(Context context,String expired) {

        getSP(context).edit().putString(Name_Struct.TrialExpired,expired).commit();

    }
    public static boolean getIsTrialExpired(Context context) {

        return Boolean.parseBoolean(getSP(context).getString(Name_Struct.TrialExpired, "false"));
    }

    public static void setPlanExpired(Context context,String expired) {

        getSP(context).edit().putString(Name_Struct.PlanExpired,expired).commit();

    }
    public static boolean getIsPlanExpired(Context context) {

        return Boolean.parseBoolean(getSP(context).getString(Name_Struct.PlanExpired, "false"));
    }


    public static void setTimeRemaining(Context context,long timeRemaining) {

        getSP(context).edit().putLong(Name_Struct.TimeRemaining,timeRemaining).commit();

    }
    public static long getTimeRemaining(Context context) {

        return getSP(context).getLong(Name_Struct.TimeRemaining, GetTrialHours(System.currentTimeMillis()));
    }


    public static long getTrackerStartedAt(Context context) {

        return getSP(context).getLong(Name_Struct.TrackerStartedAt,0);
    }

    public static void setTrackerStartedAt(Context context,long timeRemaining) {

        getSP(context).edit().putLong(Name_Struct.TrackerStartedAt,timeRemaining).commit();

    }

    public static long getTrackerStoppedAt(Context context) {

        return getSP(context).getLong(Name_Struct.TrackerStoppedAt,0);
    }

    public static void setTrackerStoppedAt(Context context,long timeRemaining) {

        getSP(context).edit().putLong(Name_Struct.TrackerStoppedAt,timeRemaining).commit();

    }

    public static long getTrackerStoppedDuration(Context context) {

        return getSP(context).getLong(Name_Struct.StoppedDuration,0);
    }

    public static void setTrackerStoppedDuration(Context context,long timeRemaining) {

        getSP(context).edit().putLong(Name_Struct.StoppedDuration,timeRemaining).commit();

    }


    public static long GetTrialHours(long date)
    {
        Calendar calendar = Calendar.getInstance();
        Date dt=new Date(date);
        calendar.setTime(dt);
        calendar.add(Calendar.HOUR, 6);
        Date dateNew=calendar.getTime();
        return calendar.getTimeInMillis();
    }
    public static void setIsTracking(Context context,Boolean isTracking) {

        getSP(context).edit().putBoolean(Name_Struct.IsTracking,isTracking).commit();

    }
    public static Boolean getIsTracking(Context context) {

        return getSP(context).getBoolean(Name_Struct.IsTracking, false);
    }


    public static void setTrackingName(Context context,String name) {

        getSP(context).edit().putString(Name_Struct.TrackingName,name).commit();

    }
    public static String getTrackingName(Context context) {

        return getSP(context).getString(Name_Struct.TrackingName, "");
    }
    public static void setTrackingCC(Context context,String cc) {

        getSP(context).edit().putString(Name_Struct.TrackingCC,cc).commit();

    }
    public static String getTrackingCC(Context context) {

        return getSP(context).getString(Name_Struct.TrackingCC, "");
    }
    public static void setTrackingNumber(Context context,String number) {

        getSP(context).edit().putString(Name_Struct.TrackingNumber,number).commit();

    }
    public static String getTrackingNumber(Context context) {

        return getSP(context).getString(Name_Struct.TrackingNumber, "");
    }






}
