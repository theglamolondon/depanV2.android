package net.djeraservices.depanv2.depanv2.webservice;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by BW.KOFFI on 24/05/2017.
 */

public interface IActionAfterHttpRequest {
    public void doSomething(String data);
    public void doSomething(JSONArray data);
}
