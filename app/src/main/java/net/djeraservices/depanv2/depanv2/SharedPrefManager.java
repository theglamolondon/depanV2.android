package net.djeraservices.depanv2.depanv2;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by BW.KOFFI on 18/02/2017.
 */

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";
    private static final String SHARE_PREF_DEPAN_V2 = "appPreference";
    public static final String APPAREIL = "appareil";
    public static final String EQUIPE = "team";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    //this method will save the device token to shared preferences
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, null);
    }

    public boolean saveEquipeID(int equipeID){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARE_PREF_DEPAN_V2,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(APPAREIL,equipeID);
        editor.apply();
        return true;
    }

    public int getEquipeIdForDevice(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARE_PREF_DEPAN_V2,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(APPAREIL,0);
    }

    public boolean storeData(String key, String data){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARE_PREF_DEPAN_V2,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();
        return true;
    }

    public String getData(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARE_PREF_DEPAN_V2,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }
}
