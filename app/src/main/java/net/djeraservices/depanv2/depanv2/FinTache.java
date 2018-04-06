package net.djeraservices.depanv2.depanv2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import net.djeraservices.depanv2.depanv2.webservice.IActionAfterHttpRequest;
import net.djeraservices.depanv2.depanv2.webservice.NatureLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinTache extends AppCompatActivity  implements View.OnClickListener{

    private TextView txtnumeroPanne;
    private View view_priorite;
    private EditText txtrapport;
    private TextView txtdateheurecontact;
    private TextView txtdateheurefinintervention;
    private TextView txtdateheuredebutintervention;
    private TextView txtdateheuredemande;
    private TextView txtdateheurearrivee;
    private TextView txtfamille;
    private TextView txtnature;
    private Button btnValidate;
    private Button btnSave;
    private RadioGroup statutGroup;
    private RadioGroup assistanceGroup;
    private RadioButton statut;
    private RadioButton assistance;
    protected ProgressDialog progressDialog;
    private Calendar calendrier = null;
    Taches tache;
    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD, Locale.FRANCE);
    String dateheure = null;
    AlertDialog.Builder familleDialog = null;
    AlertDialog.Builder natureDialog = null;
    JSONArray familles;
    JSONArray natures;
    String codefamille;
    int idNature;
    View timeView =null;

    private View.OnClickListener dateheureListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            timeView = v;
            new DatePickerDialog(FinTache.this,datepicker,calendrier.get(Calendar.YEAR),calendrier.get(Calendar.MONTH),calendrier.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendrier.set(Calendar.YEAR,year);
            calendrier.set(Calendar.MONTH,month);
            calendrier.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            new TimePickerDialog(FinTache.this,timepicker,calendrier.get(Calendar.HOUR_OF_DAY),calendrier.get(Calendar.MINUTE),true).show();
        }
    };

    private TimePickerDialog.OnTimeSetListener timepicker = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            calendrier.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendrier.set(Calendar.MINUTE, minute);

            try {
                updateValue((TextView) timeView);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    private void updateValue(TextView view) throws ParseException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        view.setText(sdf.format(calendrier.getTime()));
    }

    private IActionAfterHttpRequest natureHandler = new IActionAfterHttpRequest() {
        @Override
        public void doSomething(String data) {
            Toast.makeText(FinTache.this,data,Toast.LENGTH_LONG).show();
            progressDialog.hide();
        }

        @Override
        public void doSomething(JSONArray data) {
            natures = data;
            natureDialog = new AlertDialog.Builder(FinTache.this, R.style.Theme_AppCompat_Dialog);

            String[] viewer = new String[natures.length()];
            for(int i = 0; i < natures.length(); i++){
                try {
                    viewer[i] = (((JSONObject)natures.get(i)).getString("libelle"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            natureDialog.setTitle("Natures").setItems(viewer, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        idNature = ((JSONObject)natures.get(which)).getInt("id");
                        txtnature.setText(((JSONObject)natures.get(which)).getString("libelle"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            natureDialog.create();
            progressDialog.dismiss();
        }

        @Override
        public void error(String error) {
            //
        }
    };

    private IActionAfterHttpRequest famillyHandler = new IActionAfterHttpRequest() {
        @Override
        public void doSomething(String data) {
            Toast.makeText(FinTache.this,data,Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        @Override
        public void doSomething(JSONArray data) {
            familles = data;
            familleDialog = new AlertDialog.Builder(FinTache.this, R.style.Theme_AppCompat_Dialog);

            String[] viewer = new String[familles.length()];
            for(int i = 0; i < familles.length(); i++){
                try {
                    viewer[i] = (((JSONObject)familles.get(i)).getString("libelle"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            familleDialog.setTitle("Familles").setItems(viewer, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        codefamille = ((JSONObject)familles.get(which)).getString("code");

                        //Recupération des natures
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                progressDialog = new ProgressDialog(FinTache.this);
                                progressDialog.setMessage("Récupération des natures...");
                                progressDialog.show();

                                NatureLoader nature = new NatureLoader(DepanV2WebApi.URL_LIST_NATURE+"?code="+codefamille);
                                RequestQueue requestQueue = Volley.newRequestQueue(FinTache.this);
                                requestQueue.add(nature.getNatureOnServer(natureHandler));
                            }
                        };
                        r.run();

                        txtfamille.setText(((JSONObject)familles.get(which)).getString("libelle"));
                        //Toast.makeText(FinTache.this,codefamille,Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            familleDialog.create();

            progressDialog.dismiss();
        }

        @Override
        public void error(String error) {
            Toast.makeText(FinTache.this,"Impossible de charger les familles",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_tache);

        //Récupération des données
        /*Intent intent = getIntent();
        Taches tache = new Taches();
        tache.setNumero(intent.getStringExtra("numero"));
        tache.setPanne(intent.getStringExtra("panne"));
        tache.setLongitude(intent.getStringExtra("longitude"));
        tache.setLatitude(intent.getStringExtra("latitude"));*/

        SharedPrefManager shared = SharedPrefManager.getInstance(this);
        Gson gson = new Gson();

        this.tache = gson.fromJson(shared.getData(SharedPrefManager.BON_EN_COURS), Taches.class);


        //initialisation des champs

        this.calendrier = Calendar.getInstance(Locale.FRENCH);

        txtdateheurecontact = (TextView) findViewById(R.id.txtdateheurecontact);
        txtdateheurecontact.setText(this.tache.dateheurecontact);
        //txtdateheuredemande = (TextView) findViewById(R.id.txtdateheuredemande);
        //dateheurefinintervention = (TextView) findViewById(R.id.dateheurefinintervention);
        txtdateheuredebutintervention = (TextView) findViewById(R.id.dateheuredebutintervention);
        txtdateheuredebutintervention.setText(this.tache.datedebutintervention);
        //txtdateheurearrivee = (TextView) findViewById(R.id.txtdateheurearrivee);
        statutGroup = (RadioGroup) findViewById(R.id.radioGroup);
        //assistanceGroup = (RadioGroup) findViewById(R.id.assistanceGroup);

        this.txtnumeroPanne = (TextView) findViewById(R.id.txtnumeroPanne);
        txtnumeroPanne.setText(this.tache.numero);

        //this.view_priorite = findViewById(R.id.view_priorite);
        this.txtrapport = (EditText) findViewById(R.id.txtrapport);
        this.txtrapport.setText(this.tache.detailrapport);

        this.btnValidate = (Button) findViewById(R.id.btnValidate);
        this.btnSave = (Button) findViewById(R.id.btnSave);
        /*this.btnSave.setEnabled(false);
        this.btnSave.setBackgroundColor(Color.rgb(200,200,200));*/

        txtfamille = (TextView)findViewById(R.id.txtfamille);
        txtnature = (TextView)findViewById(R.id.txtnature);

        txtfamille.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                familleDialog.show();
            }
        });
        txtnature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                natureDialog.show();
            }
        });

        List<TextView> viewTab = new ArrayList<>();
        //viewTab.add(txtdateheuredemande);
        //viewTab.add(txtdateheurearrivee);
        viewTab.add(txtdateheurecontact);
        viewTab.add(txtdateheuredebutintervention);
        //viewTab.add(dateheurefinintervention);

        for(final TextView view : viewTab){
            view.setClickable(true);
            view.setOnClickListener(dateheureListener);
        }

        //Recupération des familles
        Runnable r = new Runnable() {
            @Override
            public void run() {
            progressDialog = new ProgressDialog(FinTache.this);
            progressDialog.setMessage("Récupération des familles de nature...");
            progressDialog.show();

            NatureLoader nature = new NatureLoader(DepanV2WebApi.URL_LIST_FAMILLE_NATURE);
            RequestQueue requestQueue = Volley.newRequestQueue(FinTache.this);
            requestQueue.add(nature.getNatureFamillyOnServer(famillyHandler));
            }
        };

        r.run();

        this.txtnumeroPanne.setText(tache.getNumero());

        this.btnValidate.setOnClickListener(this);
        this.btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == this.btnValidate){

            Log.e("SAVING","Clic sur bouton");

            progressDialog = new ProgressDialog(FinTache.this);
            progressDialog.setMessage("Sauvegarde du rapport...");
            progressDialog.show();

            Runnable r = new Runnable() {
                @Override
                public void run() {
                sendRapport(txtnumeroPanne.getText().toString(),txtrapport.getText().toString());
                }
            };
            r.run();
        }else if(v == this.btnSave){
            progressDialog = new ProgressDialog(FinTache.this);
            progressDialog.setMessage("Enregistrement du rapport sur la tablette");
            progressDialog.show();

            this.tache.detailrapport = txtrapport.getText().toString();
            this.tache.dateheurecontact = txtdateheurecontact.getText().toString();
            this.tache.dateheuredebutintervention = txtdateheuredebutintervention.getText().toString();

            SharedPrefManager shared = SharedPrefManager.getInstance(FinTache.this);
            Gson gson = new Gson();
            shared.storeData(SharedPrefManager.BON_EN_COURS,gson.toJson(this.tache));

            Log.e("##Tache",gson.toJson(this.tache));

            Intent intent = new Intent(getBaseContext(),MainActivity.class);
            startActivity(intent);

            this.finish();
        }
    }

    private void sendRapport(final String numero, final String rapportTexte)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DepanV2WebApi.URL_SEND_REPORT_TACHES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("#### Rapport ####",response);
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(FinTache.this,obj.getString("message"),Toast.LENGTH_LONG).show();

                            if(obj.getInt("code") == 0){
                                Intent intent = new Intent(FinTache.this,MainActivity.class);
                                startActivity(intent);
                            }

                            progressDialog.hide();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", "Erreur d'accès au réseau (rapport)");
                        Toast.makeText(FinTache.this,"Rapport non valide! Veuillez vérifier que tous les champs ont été remplis SVP.",Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.FRENCH);

                //params.put("android_id", androidID);
                params.put("numero", numero);
                params.put("datefinintervention", sdf.format(Calendar.getInstance().getTime()));

                //New Rapport
                statut = (RadioButton) findViewById(statutGroup.getCheckedRadioButtonId());
                //assistance = (RadioButton) findViewById(assistanceGroup.getCheckedRadioButtonId());

                params.put("rapport", rapportTexte);
                params.put("statut",statut.getText().toString());
                params.put("assistancexterne","Aucun"); //assistance.getText().toString()
                params.put("dateheurecontact",txtdateheurecontact.getText().toString());
                params.put("datedemarragenavigation",tache.datedemarragenavigation);
                params.put("dateheuredebutintervention",txtdateheuredebutintervention.getText().toString());
                params.put("dateheurefinintervention",sdf.format(Calendar.getInstance().getTime()));
                params.put("detailrapport",txtrapport.getText().toString());
                params.put("datedebutintervention", tache.datedebutintervention);
                params.put("dateheurefinnavigation",tache.dateheurefinnavigation);
                //params.put("dateheuredemande",txtdateheuredemande.getText().toString());
                //params.put("dateheurearrivee",txtdateheurearrivee.getText().toString());
                params.put("nature_id",String.valueOf(idNature));

                String equipeJSON = SharedPrefManager.getInstance(FinTache.this).getData(SharedPrefManager.EQUIPE);
                Gson gson = new Gson();
                Equipe equipe = gson.fromJson(equipeJSON, Equipe.class);

                params.put("equipe_id",String.valueOf(equipe.getId()));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        //progressDialog.dismiss();
    }
}
