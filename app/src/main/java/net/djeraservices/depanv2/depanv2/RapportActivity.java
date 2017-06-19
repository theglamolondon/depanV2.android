package net.djeraservices.depanv2.depanv2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
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

import net.djeraservices.depanv2.depanv2.location.LocationService;
import net.djeraservices.depanv2.depanv2.webservice.DepanV2WebApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RapportActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnDisplayToken;
    private Button btnSaveToken;
    private Button btnShowMain;
    private TextView txtViewToken;
    private TextView txtViewAndroidID;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapport);

        this.btnDisplayToken = (Button) findViewById(R.id.buttonDisplayToken);
        this.btnSaveToken = (Button) findViewById(R.id.buttonRegisterToken);
        this.txtViewToken = (TextView) findViewById(R.id.textViewToken);
        this.txtViewAndroidID = (TextView) findViewById(R.id.textViewAndroidID);
        this.btnShowMain = (Button) findViewById(R.id.btnMain);

        //Affichage de la l'adresse du ANDROID_ID du téléphone
        this.txtViewAndroidID.setText(android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        this.btnDisplayToken.setOnClickListener(this);
        this.btnSaveToken.setOnClickListener(this);
        this.btnShowMain.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        if (v == this.btnDisplayToken){
            this.displayToken();
        }
        if (v == this.btnSaveToken)
        {
            this.saveTokenOnServer();
        }
        if (v == this.btnShowMain)
        {
            Intent intent = new Intent(RapportActivity.this,MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        Intent intent = new Intent(RapportActivity.this, LocationService.class);
        stopService(intent);
    }

    private void saveTokenOnServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enregistrement de l'appareil en cours ...");
        progressDialog.show();

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        final String androidID = this.txtViewAndroidID.getText().toString();

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
                            Toast.makeText(RapportActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RapportActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void displayToken()
    {
        //getting token from shared preferences
        String token = SharedPrefManager.getInstance(this).getDeviceToken();

        //if token is not null
        if (token != null) {
            //displaying the token
            this.txtViewToken.setText(token);
        } else {
            //if token is null that means something wrong
            this.txtViewToken.setText("Echec de génération de token");
        }
    }
}
