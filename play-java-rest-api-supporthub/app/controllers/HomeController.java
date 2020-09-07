package controllers;

import play.mvc.*;
import javax.inject.Inject;
import java.util.Map;
import java.util.HashMap;
import play.libs.Json;
import models.dialogflow.*;
import javax.inject.Inject;
import java.util.Random;
import models.cloudsql.Connector;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    private final Integrator it;
    private final Connector conn=null;
    @Inject
    public HomeController(Integrator it) {
        this.it = it;
    }
    public Result index() throws Exception {
        String query = request().getQueryString("query");
        String n=request().getQueryString("n");
        //query="how%20are%20you";
        Map<String, String> response = new HashMap<>();
        if (query.matches("(.*)[h,H]i(.*)")) {
            response.put("content", String.format("Welcome to Support Hub"));
        } else {
            String val = java.net.URLDecoder.decode(query, "UTF-8");
            String[] inargs=new String[]{"--projectId","support-pilot","--sessionId","ces session id"+n,"--languageCode","en-US",val};
            //--projectId civic-brand-207014 --sessionId "fake session id" --languageCode en-US "looking for a gift"
            System.out.println("input text: "+val);
            String answer=it.getIntent(inargs);
           // Map<String, String> result=IntentManagement.getInput(query);
            //response.put("content", String.format("Hi, what are you looking for? "));
            response.put("content", answer);
            response.put("session", n);
        }
        return ok(Json.toJson(response));
      //  return ok(views.html.index.render());
    }
    public Result db() throws Exception {
        String inp = request().getQueryString("inp");
        String val = java.net.URLDecoder.decode(inp, "UTF-8");
        String answer=conn.readDatabase(val);
        Map<String, String> response = new HashMap<>();
        response.put("content", answer);
        return ok(Json.toJson(response));
        //  return ok(views.html.index.render());

    }
}
