package net.djeraservices.depanv2.depanv2.webservice;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by BW.KOFFI on 24/05/2017.
 */

public class NatureLoader {

    private ArrayList<String> data;
    private String url;

    public NatureLoader(String url)
    {
        this.url = url;
    }

    public StringRequest getNatureOnServer(final IActionAfterHttpRequest callback)
    {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, this.url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("# Nature response #",response);
                    try {
                        JSONArray jsonArray = new JSONArray(new String(response.getBytes("UTF-8")));
                        callback.doSomething(jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.doSomething(error.getMessage());
                }
            }
        );
        return stringRequest;
    }

    public StringRequest getNatureFamillyOnServer(final IActionAfterHttpRequest callback)
    {
        StringRequest stringRequest = new StringRequest(StringRequest.Method.GET, this.url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("##Famille response##",response);
                        try {
                            JSONArray jsonArray = new JSONArray(new String(response.getBytes("UTF-8")));
                            callback.doSomething(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.doSomething(error.getMessage());
                    }
                }
        ){
            @Override protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        return stringRequest;
    }
}
