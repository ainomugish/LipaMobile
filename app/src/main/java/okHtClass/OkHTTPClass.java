package okHtClass;

/**
 * Created by Mac on 2016/02/20.
 *
 * @author Mac
 */

/**
 *
 * @author Mac
 */

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.Pj;


public class OkHTTPClass {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    Gson gson;

    OkHttpClient client = new OkHttpClient();

    static OkHttpClient staticClient = new OkHttpClient();

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    static final Logger logger = Logger.getLogger("OkHTTPClass");

    public OkHTTPClass() {
        gson = new Gson();
    }


    //CALLS THE URL
    public static String staticPost(String url, String json) throws IOException {
        log("++++++:  OkHttpCaller static post");
        log("url:" + url);
        log("json:" + Pj.printJ(json));
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = staticClient.newCall(request).execute();
        String responseString = response.body().string();
        log("staticPost responseString:" + responseString);
        return responseString;
    }

    //POSTING A STRING
    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    //    String bowlingJson(String player1, String player2) {
//        return "{'winCondition':'HIGH_SCORE',"
//                + "'name':'Bowling',"
//                + "'round':4,"
//                + "'lastSaved':1367702411696,"
//                + "'dateStarted':1367702378785,"
//                + "'players':["
//                + "{'name':'" + player1 + "','history':[10,8,6,7,8],'color':-13388315,'total':39},"
//                + "{'name':'" + player2 + "','history':[6,10,5,10,10],'color':-48060,'total':41}"
//                + "]}";
//    }
    static void log(String msg) {
        logger.info(msg);
    }

}

