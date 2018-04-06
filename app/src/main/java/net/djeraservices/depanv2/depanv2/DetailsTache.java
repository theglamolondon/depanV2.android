package net.djeraservices.depanv2.depanv2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.djeraservices.depanv2.depanv2.MainView.Taches;
import net.djeraservices.depanv2.depanv2.webservice.DepanV2WebApi;
import net.djeraservices.depanv2.depanv2.webservice.IActionAfterHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DetailsTache extends AppCompatActivity implements View.OnClickListener {

    private String URLMaps = "google.navigation:q=%s,%s"; //google.navigation:q=latitude,longitude

    Taches tache;
    WebView webView;
    Button btnNavigation;
    Button btnReport;
    TextView txtClient;
    TextView txtPanneDetails;
    final String urlmpas = "http://maps.google.com/maps?saddr=%s,%s&daddr=%s,%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_tache);

        //Récupération des données
        Intent intent = getIntent();
        /*tache = new Taches();
        tache.setNumero(intent.getStringExtra("numero"));
        tache.setPanne(intent.getStringExtra("panne"));
        tache.setLongitude(intent.getStringExtra("longitude"));
        tache.setLatitude(intent.getStringExtra("latitude"));*/
        SharedPrefManager shared = SharedPrefManager.getInstance(this);
        Gson gson = new Gson();
        this.tache = gson.fromJson(shared.getData(SharedPrefManager.BON_EN_COURS), Taches.class);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try{
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(location != null) {
                webView = (WebView) findViewById(R.id.mapwebview);
                webView.setWebViewClient(new WebViewClient());
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setGeolocationEnabled(true);
                webView.loadUrl(String.format(urlmpas, location.getLatitude(), location.getLongitude(), tache.getLatitude(), tache.getLongitude()));
            }
        }catch (SecurityException e){
            Log.e("######Location",e.getMessage());
        }

        btnNavigation = (Button) findViewById(R.id.btnNavigation);
        btnNavigation.setOnClickListener(this);

        btnReport = (Button) findViewById(R.id.btnReport);
        btnReport.setOnClickListener(this);

        txtClient = (TextView) findViewById(R.id.txtClientName);
        txtClient.setText(tache.getNumero());

        txtPanneDetails = (TextView) findViewById(R.id.txtDetailsTache);
        txtPanneDetails.setText(tache.getPanne());

        if(!tache.dateheurefinnavigation.equals("")){
            btnNavigation.setEnabled(false);
            btnNavigation.setBackgroundColor(Color.rgb(200,200,200));
            Toast.makeText(this,"Navigation  déjà terminée",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btnNavigation){
            Log.e("#","Start Navigation");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Map<String,String> param = new HashMap<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    param.put("datedemarragenavigation", sdf.format(Calendar.getInstance().getTime()));
                    param.put("numero", tache.getNumero());

                    DepanV2WebApi api = new DepanV2WebApi(DetailsTache.this);
                    api.updateDepannageStatus(param, new IActionAfterHttpRequest() {
                        @Override
                        public void doSomething(String data) {
                            try {
                                JSONObject objet = new JSONObject(data);
                                tache.datedemarragenavigation = objet.getString("datedemarragenavigation");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void doSomething(JSONArray data) {
                            //
                        }

                        @Override
                        public void error(String error)
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            tache.datedemarragenavigation = sdf.format(Calendar.getInstance().getTime());
                        }
                    });

                    startNavigation();
                }
            };
            r.run();

        }

        if(v == btnReport){
            Map<String,String> param = new HashMap<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            param.put("datedebutintervention", sdf.format(Calendar.getInstance().getTime()));
            param.put("numero", tache.getNumero());

            DepanV2WebApi api = new DepanV2WebApi(this);
            api.updateDepannageStatus(param,  new IActionAfterHttpRequest() {
                @Override
                public void doSomething(String data) {
                    try {
                        Log.i("##API", data);
                        JSONObject objet = new JSONObject(data);
                        tache.datedebutintervention = objet.getString("datedebutintervention");
                        tache.dateheuredebutintervention = objet.getString("datedebutintervention");

                        SharedPrefManager shared = SharedPrefManager.getInstance(DetailsTache.this);
                        Gson gson = new Gson();
                        shared.storeData(SharedPrefManager.BON_EN_COURS,gson.toJson(tache));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doSomething(JSONArray data) {
                    //
                }

                @Override
                public void error(String error)
                {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    tache.datedebutintervention = sdf.format(Calendar.getInstance().getTime());
                }
            });

            Intent intent = new Intent(getBaseContext(),FinTache.class);

            //Passage des informations
            /*intent.putExtra("numero",this.tache.getNumero());
            intent.putExtra("panne",this.tache.getPanne());
            intent.putExtra("longitude",this.tache.getLongitude());
            intent.putExtra("latitude",this.tache.getLatitude());
            */

            startActivity(intent);
        }
    }

    private void startNavigation(){
        //Lancement google Maps
        Uri googleMapUri = Uri.parse(String.format(URLMaps,tache.getLatitude(),tache.getLongitude()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,googleMapUri);
        //mapIntent.setPackage("com.google.android.apps.maps");
        startActivityForResult(mapIntent,DepanV2WebApi.NAVIGATION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == DepanV2WebApi.NAVIGATION_REQUEST_CODE){
            Log.e("### Result","Résultat code : "+resultCode);
            if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED)
            {
                btnNavigation.setEnabled(false);
                btnNavigation.setBackgroundColor(Color.rgb(200,200,200));
                Toast.makeText(this,"Navigation terminée",Toast.LENGTH_LONG).show();

                //Date de contact &fin de navigation
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        Map<String,String> param = new HashMap<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        param.put("dateheurefinnavigation", sdf.format(Calendar.getInstance().getTime()));
                        param.put("numero", tache.getNumero());

                        DepanV2WebApi api = new DepanV2WebApi(DetailsTache.this);
                        api.updateDepannageStatus(param, new IActionAfterHttpRequest() {
                            @Override
                            public void doSomething(String data) {
                                try {
                                    JSONObject objet = new JSONObject(data);
                                    tache.dateheurefinnavigation = objet.getString("dateheurefinnavigation");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void doSomething(JSONArray data) {
                                //
                            }

                            @Override
                            public void error(String error)
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                tache.dateheurefinnavigation = sdf.format(Calendar.getInstance().getTime());
                            }
                        });
                    }
                };
                r.run();
            }

        }

    }
}
