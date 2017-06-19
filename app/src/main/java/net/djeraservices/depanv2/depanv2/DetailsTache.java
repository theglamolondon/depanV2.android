package net.djeraservices.depanv2.depanv2;

import android.content.Intent;
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

import net.djeraservices.depanv2.depanv2.MainView.Taches;

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
        tache = new Taches();
        tache.setNumero(intent.getStringExtra("numero"));
        tache.setPanne(intent.getStringExtra("panne"));
        tache.setLongitude(intent.getStringExtra("longitude"));
        tache.setLatitude(intent.getStringExtra("latitude"));

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

        /*
        btnNavigation = (Button) findViewById(R.id.btnNavigation);
        btnNavigation.setOnClickListener(this);
        */

        btnReport = (Button) findViewById(R.id.btnReport);
        btnReport.setOnClickListener(this);

        txtClient = (TextView) findViewById(R.id.txtClientName);
        txtClient.setText(tache.getNumero());

        txtPanneDetails = (TextView) findViewById(R.id.txtDetailsTache);
        txtPanneDetails.setText(tache.getPanne());
    }

    @Override
    public void onClick(View v) {
        if(v == btnNavigation){
            //startNavigation();
            Log.e("#","Start Navigation");
        }

        if(v == btnReport){
            Intent intent = new Intent(getBaseContext(),FinTache.class);

            //Passage des informations
            intent.putExtra("numero",this.tache.getNumero());
            intent.putExtra("panne",this.tache.getPanne());
            intent.putExtra("longitude",this.tache.getLongitude());
            intent.putExtra("latitude",this.tache.getLatitude());

            startActivity(intent);
        }
    }

    private void startNavigation(){
        //Lancement google Maps
        Uri googleMapUri = Uri.parse(String.format(URLMaps,tache.getLatitude(),tache.getLongitude()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,googleMapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
