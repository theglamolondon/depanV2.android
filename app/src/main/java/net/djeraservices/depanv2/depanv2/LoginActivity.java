package net.djeraservices.depanv2.depanv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import net.djeraservices.depanv2.depanv2.bdd.model.Appareil;
import net.djeraservices.depanv2.depanv2.webservice.DepanV2WebApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnConnexion;
    private TextView txtListeEquipe;

    public String androidID;
    private String token;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.androidID = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        this.displayToken();

        this.btnConnexion = (Button) findViewById(R.id.btnConnexion);
        this.txtListeEquipe = (TextView) findViewById(R.id.txtListeEquipe);

        this.btnConnexion.setOnClickListener(this);
        this.txtListeEquipe.setOnClickListener(this);

        this.txtListeEquipe.setText(this.androidID);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void displayToken()
    {
        //getting token from shared preferences
        this.token = SharedPrefManager.getInstance(this).getDeviceToken();

        //if token is not null
        if (token == null) {
            //if token is null that means something wrong
            Toast.makeText(this,"Echec de génération du token",Toast.LENGTH_SHORT).show();
        }
    }

    public void saveDevice(Appareil appareil){
        Gson gson = new Gson();
        SharedPrefManager.getInstance(this).storeData(SharedPrefManager.APPAREIL,gson.toJson(appareil));
    }

    public void showConnexion(){
        Intent intent = new Intent(getBaseContext(),DemarrageActivity.class);
        startActivity(intent);
    }

    private void saveTokenOnServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enregistrement de l'appareil en cours ...");
        progressDialog.show();

        final String token = this.token;
        final String androidID = this.androidID;

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token non généré ...", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DepanV2WebApi.URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();

                            //Sauvvegarde de l'appareil
                            JSONObject equipeJson = obj.getJSONObject("appareil");

                            Appareil appareil = new Appareil();
                            appareil.setId(equipeJson.getInt("id"));
                            appareil.setAndroid_id(equipeJson.getString("android_id"));
                            appareil.setLibelle(equipeJson.getString("libelle"));
                            appareil.setToken(equipeJson.getString("token"));

                            saveDevice(appareil);

                            //Lancement de l'instance
                            showConnexion();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("android_id", androidID);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {
        if(v == this.btnConnexion){
            saveTokenOnServer();
        }
    }
}