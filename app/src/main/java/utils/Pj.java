package utils;

/**
 * Created by Mac on 2016/02/20.
 */


import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * @author Mac
 */
public class Pj {

    public static String printJ(String json) {

        String prettyJson = new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(json));
        // System.out.println(prettyJson);

        return prettyJson;

    }

}

