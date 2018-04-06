package net.djeraservices.depanv2.depanv2.webservice;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import net.djeraservices.depanv2.depanv2.LoginActivity;
import net.djeraservices.depanv2.depanv2.MainView.Taches;
import net.djeraservices.depanv2.depanv2.RapportActivity;
import net.djeraservices.depanv2.depanv2.SharedPrefManager;
import net.djeraservices.depanv2.depanv2.bdd.model.Appareil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 19/02/2017.
 */

public class DepanV2WebApi {
    private Context context;
    private RequestQueue requestQueue ;

    //private static final String URLBase = "https://dev.djera-services.net/public/devices";
    private static final String URLBase = "http://10.109.6.106/public/devices";
    public static final String URL_SEND_GPS_LOCATION  = URLBase + "/register/location";
    public static final String URL_GET_TACHES_LIST    = URLBase + "/%s/taches";
    public static final String URL_SEND_REPORT_TACHES = URLBase + "/rapport";
    public static final String URL_MAJ_DEPANNAGE = URLBase + "/depannage/status";
    public static final String URL_LIST_FAMILLE_NATURE = URLBase + "/famille";
    public static final String URL_LIST_NATURE = URLBase + "/nature";
    public static final String URL_LIST_EQUIPE = URLBase + "/equipes";
    public static final String URL_REGISTER_DEVICE = URLBase + "/register/token";
    public static final String URL_AUTH_EQUIPE = URLBase + "/auth/equipe";
    public static final String URL_LOGOUT_EQUIPE = URLBase + "/auth/equipe/logout";

    public static final int NAVIGATION_REQUEST_CODE = 1234;


    public DepanV2WebApi(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(this.context);
    }

    public void storeGpsPosition(final String longitude, final String latitude, final String speed, final String androidID){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SEND_GPS_LOCATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("### STORE GPS ###",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyErrorGPS", "Erreur d'accès au réseau (gps)");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("longitude", longitude);
                params.put("latitude", latitude);
                params.put("speed", speed);
                params.put("android_id", androidID);
                return params;
            }
        };
        this.requestQueue.add(stringRequest);
    };

    public void updateDepannageStatus(final Map<String,String> parametres, final IActionAfterHttpRequest action){
        final Context ctxt = this.context;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MAJ_DEPANNAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("### MAJ ###",response);
                        action.doSomething(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyErrorGPS", "Erreur d'accès au réseau (gps)");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Gson gson = new Gson();
                Appareil appareil = gson.fromJson(SharedPrefManager.getInstance(ctxt).getData(SharedPrefManager.APPAREIL),Appareil.class);
                parametres.put("android_id", appareil.getAndroid_id());
                return parametres;
            }
        };
        this.requestQueue.add(stringRequest);
    }

    public void login(final Map<String, String> params, final IActionAfterHttpRequest callback){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_AUTH_EQUIPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String json = null;
                        try {
                            json = new String(response.getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        callback.doSomething(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERREUR", "Une erreur est survenue. "+error.getMessage());
                        callback.error("Une erreur est survenue. ");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        this.requestQueue.add(stringRequest);
    }

    public void logout(final IActionAfterHttpRequest callback){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGOUT_EQUIPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String json = null;
                        try {
                            json = new String(response.getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        callback.doSomething(json);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERREUR", "Une erreur est survenue"+error.getMessage());
                        callback.error("Une erreur est survenue.");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Gson gson = new Gson();
                Appareil appareil = gson.fromJson(SharedPrefManager.getInstance(context).getData(SharedPrefManager.APPAREIL),Appareil.class);
                params.put("android_id", appareil.getAndroid_id());
                return params;
            }
        };
        this.requestQueue.add(stringRequest);
    }
}
