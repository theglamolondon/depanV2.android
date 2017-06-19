package net.djeraservices.depanv2.depanv2.MainView;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.djeraservices.depanv2.depanv2.DetailsTache;
import net.djeraservices.depanv2.depanv2.LoginActivity;
import net.djeraservices.depanv2.depanv2.R;
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

    public TacheAdapter(Context context, List<Taches> taches) {
        super(context, 0, taches);
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.liste_tache_layout,parent,false);
        }

        TacheViewHolder tacheViewHolder = (TacheViewHolder) convertView.getTag();
        if(tacheViewHolder == null){
            tacheViewHolder = new TacheViewHolder();
            tacheViewHolder.avatar_tool = (ImageView) convertView.findViewById(R.id.avatar_tool);
            tacheViewHolder.txtclient = (TextView) convertView.findViewById(R.id.txtclient);
            tacheViewHolder.txtDetails = (TextView) convertView.findViewById(R.id.txtDetails);
            tacheViewHolder.btnMap = (Button) convertView.findViewById(R.id.btnMap);
            tacheViewHolder.btnRapport = (Button) convertView.findViewById(R.id.btnRapport);
            tacheViewHolder.rLayout = (RelativeLayout) convertView.findViewById(R.id.rLayout);

            convertView.setTag(tacheViewHolder);
        }

        final Taches taches = getItem(position);
        tacheViewHolder.avatar_tool.setImageResource(R.drawable.tools);
        tacheViewHolder.txtclient.setText(taches.getNumero());
        tacheViewHolder.txtDetails.setText(taches.getPanne());

        //tacheViewHolder.rLayout.set

        if(taches.getPriorite() == taches.PRIORITE_TRES_URGENT)
            tacheViewHolder.rLayout.setBackgroundColor(Color.rgb(255, 170, 170));

        if(taches.getPriorite() == taches.PRIORITE_URGENT)
            tacheViewHolder.rLayout.setBackgroundColor(Color.rgb(255, 233, 186));

        if(taches.getPriorite() == taches.PRIORITE_NORMALE)
            tacheViewHolder.rLayout.setBackgroundColor(Color.rgb(221, 230, 255));

        tacheViewHolder.btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification au serveur Web
                Map<String,String> param = new HashMap<String, String>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                param.put("datedemarragenavigation", sdf.format(Calendar.getInstance().getTime()));
                param.put("numero", taches.getNumero());

                DepanV2WebApi api = new DepanV2WebApi(getContext());
                api.updateDepannageStatus(param);

                startNavigation(taches);
            }
        });

        tacheViewHolder.btnRapport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification au serveur Web
                Map<String,String> param = new HashMap<String, String>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                param.put("datedebutintervention", sdf.format(Calendar.getInstance().getTime()));
                param.put("numero", taches.getNumero());

                DepanV2WebApi api = new DepanV2WebApi(getContext());
                api.updateDepannageStatus(param);

                showDetails(taches);
            }
        });

        return convertView;
    }

    private void showDetails(Taches t){
        Intent intent = new Intent(getContext(),DetailsTache.class);

        //Passage des informations
        intent.putExtra("numero",t.getNumero());
        intent.putExtra("panne",t.getPanne());
        intent.putExtra("longitude",t.getLongitude());
        intent.putExtra("latitude",t.getLatitude());

        getContext().startActivity(intent);
    }
    private void startNavigation(Taches tache){
        //Lancement google Maps
        Uri googleMapUri = Uri.parse(String.format(URLMaps,tache.getLatitude(),tache.getLongitude()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW,googleMapUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        getContext().startActivity(mapIntent);
    }

    private class TacheViewHolder{
        public ImageView avatar_tool;
        public TextView txtclient;
        public TextView txtDetails;
        public Button btnMap;
        public Button btnRapport;
        public RelativeLayout rLayout;
    }
}
