package net.djeraservices.depanv2.depanv2.MainView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import net.djeraservices.depanv2.depanv2.DetailsTache;
import net.djeraservices.depanv2.depanv2.LoginActivity;
import net.djeraservices.depanv2.depanv2.R;
import net.djeraservices.depanv2.depanv2.SharedPrefManager;
import net.djeraservices.depanv2.depanv2.webservice.DepanV2WebApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by BW.KOFFI on 21/02/2017.
 */

public class TacheAdapter extends ArrayAdapter<Taches> {

    private String URLMaps = "google.navigation:q=%s,%s"; //google.navigation:q=latitude,longitude
    private Context context;

    public TacheAdapter(Context context, List<Taches> taches) {
        super(context, 0, taches);
        this.context = context;
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.liste_tache_layout,parent,false);
        }

        TacheViewHolder tacheViewHolder = (TacheViewHolder) convertView.getTag();
        if(tacheViewHolder == null){
            tacheViewHolder = new TacheViewHolder();
            tacheViewHolder.view_priorite = convertView.findViewById(R.id.view_priorite);
            tacheViewHolder.txtclient = (TextView) convertView.findViewById(R.id.txtclient);
            tacheViewHolder.txtDetails = (TextView) convertView.findViewById(R.id.txtDetails);
            tacheViewHolder.btnMap = (Button) convertView.findViewById(R.id.btnMap);
            tacheViewHolder.btnRapport = (Button) convertView.findViewById(R.id.btnRapport);
            tacheViewHolder.rLayout = (RelativeLayout) convertView.findViewById(R.id.rLayout);

            convertView.setTag(tacheViewHolder);
        }

        final Taches taches = getItem(position);
        //tacheViewHolder.avatar_tool.setImageResource(R.drawable.tools);
        tacheViewHolder.txtclient.setText(taches.getNumero());
        tacheViewHolder.txtDetails.setText(taches.getPanne());

        //tacheViewHolder.rLayout.set

        if(tacheViewHolder.view_priorite != null)
        {
            if(taches.getPriorite() == taches.PRIORITE_TRES_URGENT)
                tacheViewHolder.view_priorite.setBackgroundColor(Color.rgb(224, 11, 11));

            if(taches.getPriorite() == taches.PRIORITE_URGENT)
                tacheViewHolder.view_priorite.setBackgroundColor(Color.rgb(237, 125, 11));

            if(taches.getPriorite() == taches.PRIORITE_NORMALE)
                tacheViewHolder.view_priorite.setBackgroundColor(Color.rgb(63, 162, 255));
        }

        tacheViewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification au serveur Web => retrait à cause de la suppression des boutons sur la liste
                /*Map<String,String> param = new HashMap<String, String>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                param.put("datedemarragenavigation", sdf.format(Calendar.getInstance().getTime()));
                param.put("numero", taches.getNumero());

                DepanV2WebApi api = new DepanV2WebApi(getContext());
                api.updateDepannageStatus(param);*/
                startNavigation(taches);
            }
        });

        tacheViewHolder.btnRapport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification au serveur Web => retrait à cause de la suppression des boutons sur la liste
                /*Map<String,String> param = new HashMap<String, String>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                param.put("datedebutintervention", sdf.format(Calendar.getInstance().getTime()));
                param.put("numero", taches.getNumero());

                DepanV2WebApi api = new DepanV2WebApi(getContext());
                api.updateDepannageStatus(param); */
                showDetails(taches);
            }
        });
        return convertView;
    }

    private void showDetails(final Taches tache){
        final SharedPrefManager shared = SharedPrefManager.getInstance(this.context);

        final Gson gson = new Gson();
        String cacheData = shared.getData(SharedPrefManager.BON_EN_COURS);
        if( cacheData != null)
        {
            Taches cache = gson.fromJson(cacheData, Taches.class);
            if( !cache.getNumero().equals(tache.getNumero()) )
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                builder.setMessage("Voulez vous ouvrir cette reclamation ? Les modifications sur la derniere reclamation sauvegardée seront annulées.");
                builder.setCancelable(false);

                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        shared.storeData(SharedPrefManager.BON_EN_COURS,gson.toJson(tache));

                        Intent intent = new Intent(getContext(),DetailsTache.class);

                        //Passage des informations
                        /*intent.putExtra("numero",tache.getNumero());
                        intent.putExtra("panne",tache.getPanne());
                        intent.putExtra("longitude",tache.getLongitude());
                        intent.putExtra("latitude",tache.getLatitude());*/

                        getContext().startActivity(intent);
                    }
                });

                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }else{
                Intent intent = new Intent(getContext(),DetailsTache.class);

                //Passage des informations
                        /*intent.putExtra("numero",tache.getNumero());
                        intent.putExtra("panne",tache.getPanne());
                        intent.putExtra("longitude",tache.getLongitude());
                        intent.putExtra("latitude",tache.getLatitude());*/

                getContext().startActivity(intent);
            }
        }else{
            shared.storeData(SharedPrefManager.BON_EN_COURS,gson.toJson(tache));
        }
    }
    private void startNavigation(Taches tache){
        //Lancement google Maps
        Uri googleMapUri = Uri.parse(String.format(URLMaps,tache.getLatitude(),tache.getLongitude()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,googleMapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        getContext().startActivity(mapIntent);
    }

    private class TacheViewHolder{
        public View view_priorite;
        public TextView txtclient;
        public TextView txtDetails;
        public Button btnMap;
        public Button btnRapport;
        public RelativeLayout rLayout;
    }
}
