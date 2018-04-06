package net.djeraservices.depanv2.depanv2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.djeraservices.depanv2.depanv2.bdd.model.Appareil;
import net.djeraservices.depanv2.depanv2.bdd.model.Equipe;
import net.djeraservices.depanv2.depanv2.webservice.DepanV2WebApi;
import net.djeraservices.depanv2.depanv2.webservice.IActionAfterHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConnexionActivity extends AppCompatActivity implements View.OnClickListener{

    private AlertDialog.Builder equipeDialog;
    private TextView txtequipeLogin;
    private EditText txtpassword;
    private Button btn_connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        txtequipeLogin = (TextView) findViewById(R.id.txtequipeLogin);
        txtpassword = (EditText) findViewById(R.id.txtpassword);
        btn_connexion = (Button) findViewById(R.id.btn_connexion);

        btn_connexion.setOnClickListener(this);

        Intent intent = getIntent();
        String json = intent.getStringExtra("equipes");

        //Log.e("EQUIPES",json);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<Equipe> equipes = getEquipes(jsonArray);

        final ArrayList<Equipe> _equipes = equipes;

        //this.getActionBar().hide();

        txtequipeLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equipeDialog = new AlertDialog.Builder(ConnexionActivity.this, R.style.Theme_AppCompat_Dialog);

                String[] viewer = new String[_equipes.size()];
                for(int i = 0; i < _equipes.size(); i++){
                    viewer[i] = _equipes.get(i).getLibelle();
                }

                equipeDialog.setTitle("Equipes DEPAN v2").setItems(viewer, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectEquipe(_equipes.get(which));
                    }
                });
                equipeDialog.create();
                equipeDialog.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //outState.put
    }

    private ArrayList<Equipe> getEquipes(JSONArray jsonArray){
        ArrayList<Equipe> equipes = new ArrayList<>();
        try{
            final int total = jsonArray.length()-1;
            for(int i=0; i <= total ; i++){
                JSONObject objectRaw = (JSONObject)jsonArray.get(i);

                equipes.add(new Equipe(objectRaw.getInt("id"),
                        objectRaw.getString("login"),
                        objectRaw.getString("libelle"),
                        objectRaw.getString("datecreation"))
                );
            }
        }catch (JSONException e){
            Log.e("JSON Error",e.getMessage());
        }
        return equipes;
    }

    private void selectEquipe(Equipe equipe){
        txtequipeLogin.setText(equipe.getLibelle());
        txtequipeLogin.setTag(equipe);
    }

    @Override
    public void onClick(View v) {
        if(v == this.btn_connexion){
            this.login();
        }
    }

    private void login()
    {
        if(txtequipeLogin.getTag() == null){
            Toast.makeText(this,"Veuillez choisir votre équipe SVP",Toast.LENGTH_SHORT).show();
            return;
        }
        if(txtpassword.getText().toString().equals("")){
            Toast.makeText(this,"Veuillez saisir votre mot de passe SVP",Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Authentification de l'équipe...");
        progressDialog.show();

        Gson gson = new Gson();
        Appareil appareil = gson.fromJson(SharedPrefManager.getInstance(ConnexionActivity.this).getData(SharedPrefManager.APPAREIL),Appareil.class);

        Map<String,String> input = new HashMap<>();
        input.put("login",((Equipe)txtequipeLogin.getTag()).getLogin());
        input.put("password",txtpassword.getText().toString());
        input.put("android_id",appareil.getAndroid_id());

        DepanV2WebApi api = new DepanV2WebApi(this);
        api.login(input, new IActionAfterHttpRequest() {
            @Override
            public void doSomething(String data) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    Toast.makeText(ConnexionActivity.this,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();

                    if(jsonObject.getInt("code") == 1){ //Succès code = 1 && error = 0
                        //Sauvergade de l'équipe
                        storeTeam((Equipe)txtequipeLogin.getTag());

                        Intent intent = new Intent(ConnexionActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doSomething(JSONArray data) {

            }

            @Override
            public void error(String error) {
                Toast.makeText(ConnexionActivity.this,"Erreur d'accès à internet. "+error,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void storeTeam(Equipe equipe){
        Gson gson = new Gson();
        String equipeJson = gson.toJson(equipe);
        SharedPrefManager.getInstance(this).storeData(SharedPrefManager.EQUIPE,equipeJson);
    }
}