package net.djeraservices.depanv2.depanv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import net.djeraservices.depanv2.depanv2.MainView.Taches;
import net.djeraservices.depanv2.depanv2.bdd.model.Equipe;
import net.djeraservices.depanv2.depanv2.webservice.DepanV2WebApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DemarrageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demarrage);

        //Vérification si le device est enregistré ou non
        //String token = SharedPrefManager.getInstance(this).getDeviceToken();
        String appareilJson = SharedPrefManager.getInstance(this).getData(SharedPrefManager.APPAREIL);
        String equipeJson = SharedPrefManager.getInstance(this).getData(SharedPrefManager.EQUIPE);

        if(appareilJson == null){
            Intent intent = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(intent);
        }else if(equipeJson == null){
            //Vérification qu'une équipe est connectée
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    getEquipesFromServer();
                }
            };
            r.run();
        }else{
            Intent intent = new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);
        }
    }

    private void goToConnectWithTeam(String json){
        //On passe à l'écran de connexion
        Intent intent = new Intent(DemarrageActivity.this, ConnexionActivity.class);

        intent.putExtra("equipes",json);
        startActivity(intent);
    }

    private void getEquipesFromServer(){
        //URL_LIST_EQUIPE
        StringRequest stringRequest = new StringRequest(Request.Method.GET, String.format(DepanV2WebApi.URL_LIST_EQUIPE),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("####Equipe",response);
                            String EquipeJson = new String(response.getBytes("UTF-8"));
                            goToConnectWithTeam(EquipeJson);

                        } catch (UnsupportedEncodingException e)   {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("LISTE_EQUIPE", "Erreur d'accès à internet."+error.getMessage());
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}