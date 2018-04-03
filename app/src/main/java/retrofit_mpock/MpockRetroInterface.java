package retrofit_mpock;

import models.Minput;
import models.Mreply;
import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by ivan on 7/8/2015.
 */
public interface MpockRetroInterface {

    ///http://52.11.173.17:8080/rosca/webresources/userRestService/clientPoints

//    @POST("/mpocketmav-1.0-SNAPSHOT/webresources/webgui/listStudents")
//    void getStudentList(@Body Minput minput, Callback<Mreply> mr);

    @POST("/mpocketmav-1.0-SNAPSHOT/webresources/webgui/listStudents")
    Call<Mreply> getStudentList(@Body Minput minput);

    //SEND CANTEEN TRANS JOB
    @POST("/mpocketmav-1.0-SNAPSHOT/webresources/app/postCanteenTrans")
    Call<Mreply> canteenPost(@Body Minput minput);

   // void getStudentList(@Body Minput minput);

  //  Call<Item> createUser(@Body String name, @Body String email);

//    @POST("/user/create")
//    Call<Item> createUser(@Body String name, @Body String email);

//    @POST("/rosca/webresources/userRestService/rating")
//    void sendClientRating(@Body SusuTrans sts, Callback<SusuTrans> st);
//
//    @POST("/rosca/webresources/userRestService/getGps")
//    void getCurrentGps(@Body SusuTrans sts, Callback<SusuTrans> st);
////send complete sus
//    @POST("/rosca/webresources/roscaGroup/completeSusu")
//    void sendCompleteSusuDetails(@Body SusuTrans sts, Callback<SusuTrans> st);
////reload group gps[from server]
//    @POST("/rosca/webresources/roscaGroup/groupReload")
//    void reloadGroupStatus(@Body SusuTrans sts, Callback<SusuTrans> st);
////join group
//    @POST("/rosca/webresources/userRestService/addmember")
//    void joingroup(@Body SusuTrans sts, Callback<SusuTrans> st);
//   /* @POST("/user/login")
//    void login(@Body SusuTrans sts,
//               RestCallback<SusuTrans> callback);*/
//
//    //no to restarting group
//    @POST("rosca/webresources/roscaGroup/rejectGroupRestart")
//    void noRestart(@Body SusuTrans sts, Callback<SusuTrans> st);
}
