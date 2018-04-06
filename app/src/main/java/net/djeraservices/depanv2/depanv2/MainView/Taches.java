package net.djeraservices.depanv2.depanv2.MainView;

/**
 * Created by BW.KOFFI on 21/02/2017.
 */

public class Taches {
    public int id = 0;
    public int priorite;
    public String numero;
    public String panne;
    public String longitude;
    public String latitude;
    public String detailrapport;

    public String dateaffectation;
    public String datedemarragenavigation;
    public String dateheurecontact;
    public String datedebutintervention;
    public String dateheuredebutintervention;
    public String dateheurefinnavigation;

    public final int PRIORITE_TRES_URGENT = 1;
    public final int PRIORITE_URGENT = 2;
    public final int PRIORITE_NORMALE = 3;

    public Taches(int id, int priorite, String numero, String panne, String longitude, String latitude, String dateaffectation) {
        this.id = id;
        this.priorite = priorite;
        this.numero = numero;
        this.panne = panne;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dateaffectation = dateaffectation;

        this.datedebutintervention = "";
        this.dateheurefinnavigation = "";
        this.dateheurecontact = "";
    }

    public Taches(){
        id = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriorite() {
        return priorite;
    }

    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPanne() {
        return panne;
    }

    public void setPanne(String panne) {
        this.panne = panne;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDateaffectation() {
        return dateaffectation;
    }

    public void setDateaffectation(String dateaffectation) {
        this.dateaffectation = dateaffectation;
    }
}
