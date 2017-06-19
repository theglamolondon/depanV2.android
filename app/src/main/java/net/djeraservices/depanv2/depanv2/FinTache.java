package net.djeraservices.depanv2.depanv2;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import net.djeraservices.depanv2.depanv2.MainView.Taches;
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
    private ImageView avatar_tools;
    private EditText txtrapport;
    private TextView txtdateheurecontact;
    private TextView dateheurefinintervention;
    private TextView dateheuredebutintervention;
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
        view.setText(calendrier.get(Calendar.DAY_OF_MONTH)+"/"+(calendrier.get(Calendar.MONTH)+1)+"/"+calendrier.get(Calendar.YEAR)+" "+calendrier.get(Calendar.HOUR_OF_DAY)+":"+calendrier.get(Calendar.MINUTE));
        //txtdebut.setText(dateFormat.format(calendrier.getTime()));
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
            progressDialog.hide();
        }
    };

    private IActionAfterHttpRequest famillyHandler = new IActionAfterHttpRequest() {
        @Override
        public void doSomething(String data) {
            Toast.makeText(FinTache.this,data,Toast.LENGTH_LONG).show();
            progressDialog.hide();
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

            progressDialog.hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_tache);

        this.calendrier = Calendar.getInstance(Locale.FRENCH);

        txtdateheurecontact = (TextView) findViewById(R.id.txtdateheurecontact);
        txtdateheuredemande = (TextView) findViewById(R.id.txtdateheuredemande);
        dateheurefinintervention = (TextView) findViewById(R.id.dateheurefinintervention);
        dateheuredebutintervention = (TextView) findViewById(R.id.dateheuredebutintervention);
        txtdateheurearrivee = (TextView) findViewById(R.id.txtdateheurearrivee);
        statutGroup = (RadioGroup) findViewById(R.id.radioGroup);
        assistanceGroup = (RadioGroup) findViewById(R.id.assistanceGroup);

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
        viewTab.add(txtdateheuredemande);
        viewTab.add(txtdateheurearrivee);
        viewTab.add(txtdateheurecontact);
        viewTab.add(dateheuredebutintervention);
        viewTab.add(dateheurefinintervention);

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

        //Récupération des données
        Intent intent = getIntent();
        Taches tache = new Taches();
        tache.setNumero(intent.getStringExtra("numero"));
        tache.setPanne(intent.getStringExtra("panne"));
        tache.setLongitude(intent.getStringExtra("longitude"));
        tache.setLatitude(intent.getStringExtra("latitude"));

        this.txtnumeroPanne = (TextView) findViewById(R.id.txtnumeroPanne);
        this.avatar_tools = (ImageView) findViewById(R.id.avatar_tool);
        this.txtrapport = (EditText) findViewById(R.id.txtrapport);

        this.btnValidate = (Button) findViewById(R.id.btnValidate);
        this.btnSave = (Button) findViewById(R.id.btnSave);

        this.txtnumeroPanne.setText(tache.getNumero());

        this.btnValidate.setOnClickListener(this);
        this.btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == this.btnSave || v == this.btnValidate){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sauvegarde du rapport...");
            progressDialog.show();

            this.sendRapport(this,android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),this.txtnumeroPanne.getText().toString(),this.txtrapport.getText().toString());
        }
    }

    private void sendRapport(final Context ctx, final String androidID, final String numero, final String rapportTexte)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DepanV2WebApi.URL_SEND_REPORT_TACHES,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("#### Rapport ####",response);
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(ctx,"Rapport enregistré avec succès",Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(FinTache.this,MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.hide();
                        Log.e("VolleyError", "Erreur d'accès au réseau (rapport)");

                        Toast.makeText(ctx,"Rapport non valide! Veuillez vérifier que tous les champs ont été remplis SVP.",Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.FRENCH);

                params.put("rapport", rapportTexte);
                params.put("android_id", androidID);
                params.put("numero", numero);
                params.put("datefinintervention", sdf.format(Calendar.getInstance().getTime()));

                //New Rapport
                statut = (RadioButton) findViewById(statutGroup.getCheckedRadioButtonId());
                assistance = (RadioButton) findViewById(assistanceGroup.getCheckedRadioButtonId());

                params.put("statut",statut.getText().toString());
                params.put("assistancexterne",assistance.getText().toString());
                params.put("dateheurecontact",txtdateheurecontact.getText().toString());
                params.put("dateheuredebutintervention",dateheuredebutintervention.getText().toString());
                params.put("dateheurefinintervention",dateheurefinintervention.getText().toString());
                params.put("detailrapport",txtrapport.getText().toString());
                params.put("dateheuredemande",txtdateheuredemande.getText().toString());
                params.put("dateheurearrivee",txtdateheurearrivee.getText().toString());
                params.put("nature_id",String.valueOf(idNature));

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        progressDialog.dismiss();
    }
}
