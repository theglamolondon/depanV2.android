package net.djeraservices.depanv2.depanv2.bdd.model;

import java.io.Serializable;

/**
 * Created by BW.KOFFI on 18/06/2017.
 */

public class Equipe implements Serializable {
    /*
    *"id": 1,
        "login": "equipe1",
        "libelle": "Equipe 1",
        "datecreation": "2017-05-28 13:39:41"
    * */

    private int id;
    private String login;
    private String libelle;
    private String datecreation;

    public Equipe(int id, String login, String libelle, String datecreation){
        this.id = id;
        this.login = login;
        this.libelle = libelle;
        this.datecreation = datecreation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(String datecreation) {
        this.datecreation = datecreation;
    }
}
