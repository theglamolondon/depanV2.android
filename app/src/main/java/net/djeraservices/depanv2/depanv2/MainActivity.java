package net.djeraservices.depanv2.depanv2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import net.djeraservices.depanv2.depanv2.MainView.TacheAdapter;
import net.djeraservices.depanv2.depanv2.MainView.Taches;
import net.djeraservices.depanv2.depanv2.bdd.model.Equipe;
import net.djeraservices.depanv2.depanv2.location.LocationService;
import net.djeraservices.depanv2.depanv2.webservice.DepanV2WebApi;
import net.djeraservices.depanv2.depanv2.webservice.IActionAfterHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView mList;
    public static Equipe equipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Récupération de l'équipe
        String json  = SharedPrefManager.getInstance(MainActivity.this).getData(SharedPrefManager.EQUIPE);
        Gson gson = new Gson();
        MainActivity.equipe = gson.fromJson(json,Equipe.class);

        //Mise a jour de la barre
        this.setTitle("Liste des tâches - "+equipe.getLibelle());

        this.mList = (ListView) findViewById(R.id.list_taches);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Vérification si le device est enregistré ou non
        /*
        String token = SharedPrefManager.getInstance(this).getDeviceToken();
        int equipe = SharedPrefManager.getInstance(this).getEquipeIdForDevice();

        if(equipe == 0){
            Intent intent = new Intent(getBaseContext(),LoginActivity.class);
            startActivity(intent);
        }
        */

        this.getListeTache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    private void fillListView(final List<Taches> listeTaches){
        TacheAdapter adapter = new TacheAdapter(MainActivity.this,listeTaches);
        mList.setAdapter(adapter);

        mList.setClickable(true);
    }

    protected void changeBarName(String name){
        //this.setTitle("Liste des tâches - "+name);
    }

    private void getListeTache(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Récupération des taches en cours ...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, String.format(DepanV2WebApi.URL_GET_TACHES_LIST,equipe.getId()),

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            //Log.e("####Taches",response);
                            JSONObject jsonObject = new JSONObject(new String(response.getBytes("UTF-8")));

                            //Change Bar Title
                            changeBarName(jsonObject.getString("equipe"));

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            final List<Taches> taches = generateTaches(jsonArray);
                            fillListView(taches);

                            progressDialog.dismiss();

                        } catch (UnsupportedEncodingException e)   {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("VolleyErrorTaches", "Erreur d'accès au réseau."+error.getMessage());
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("equipe_id", ""+equipe.getId());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private List<Taches> generateTaches(JSONArray json)
    {
        List<Taches> taches = new ArrayList<>();
        try{
            final int total = json.length()-1;
            for(int i=0; i <= total ; i++){
                JSONObject objectRaw = (JSONObject)json.get(i);
                taches.add(new Taches(objectRaw.getInt("id"),
                        objectRaw.getInt("priorite"),
                        objectRaw.getString("numero"),
                        objectRaw.getString("Panne"),
                        objectRaw.getString("long"),
                        objectRaw.getString("lat"),
                        objectRaw.getString("dateaffectation")));
            }
        }catch (JSONException e){
            Log.e("JSON Error",e.getMessage());
        }
        return taches;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_logout :
                logout();
                return true;

            case R.id.app_bar_quart:
                showQuart();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout()
    {
        Toast.makeText(this,"Déconnexion de l'équipe",Toast.LENGTH_LONG).show();
        SharedPrefManager.getInstance(this).deleteData(SharedPrefManager.EQUIPE);

        Runnable r = new Runnable() {
            @Override
            public void run() {
                DepanV2WebApi api = new DepanV2WebApi(MainActivity.this);
                api.logout(new IActionAfterHttpRequest() {
                    @Override
                    public void doSomething(String data) {
                        startLoginView();
                    }

                    @Override
                    public void doSomething(JSONArray data) {
                        startLoginView();
                    }

                    @Override
                    public void error(String error) {
                        Toast.makeText(MainActivity.this,error,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        r.run();

    }

    private void startLoginView(){
        Intent intent = new Intent(MainActivity.this,DemarrageActivity.class);
        startActivity(intent);
    }

    private void showQuart()
    {
        Toast.makeText(this,"Quart des équipes",Toast.LENGTH_LONG).show();
    }
}