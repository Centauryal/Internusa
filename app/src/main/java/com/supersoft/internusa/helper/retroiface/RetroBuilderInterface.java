package com.supersoft.internusa.helper.retroiface;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.supersoft.internusa.model.Cities;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.PojoAktifasi;
import com.supersoft.internusa.model.PojoDefault;
import com.supersoft.internusa.model.PojoGetBalance;
import com.supersoft.internusa.model.PojoLoadVoucher;
import com.supersoft.internusa.model.PojoProfilUsaha;
import com.supersoft.internusa.model.PojoResponseConfirm;
import com.supersoft.internusa.model.PojogetBank;
import com.supersoft.internusa.model.ProfesiParamModel;
import com.supersoft.internusa.model.Timelinetujuan;
import com.supersoft.internusa.model.mProfesi;
import com.supersoft.internusa.model.mUsaha;
import com.supersoft.internusa.ui.payment.PojoLoadProvider;
import com.supersoft.internusa.ui.sidebar.phoneContactObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Centaury on 19/04/2018.
 */
public interface RetroBuilderInterface {
    @GET("cetakstruk/cetak")
    @Streaming
    Call<ResponseBody> downloadFile(@Query("idpel") String idpel, @Query("tgl") String tgl);

    @POST("/androidapp/controller.v1.php")
    Call<PojoLoadProvider> loadProvider(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<PojoAktifasi> getDataAktifasi(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<PojoGetBalance> getBalance(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<PojoLoadVoucher> loadVoucher(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<PojoResponseConfirm> confirmationIsiulang(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<PojogetBank> loadBank(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<JsonObject> historyPembayara(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<JsonObject> registrasi_from_app(@Body ModelRequestInfo modelinfo);

    @POST("/androidapp/controller.v1.php")
    Call<JsonObject> onSubmitDefault(@Body ModelRequestInfo modelinfo);

    /*

    segment ke socmed

     */


    @POST
    Call<PojoProfilUsaha> getProfilUsahaRekan(@Url String url, @Body ModelRequestInfo json);

    //@POST("android/v2/index.php/{controller}/get_info/{crud}/{query}")
    //Call<PojoProfilUsaha> get_info(@Path("controller") String crud, @Path("query") String query, @Body ModelRequestInfo json);

    @FormUrlEncoded
    @POST
    Call<Timelinetujuan> getTujuanTimeline(@Url String url, @Field("deviceid") String deviceid, @Field("hp") String hp, @Field("mitraid") String mitraid);

    @POST
    Call<JsonObject> setLikes(@Url String url, @Body ModelRequestInfo json);

    @POST
    Call<JsonObject> getDetailprofilRekan(@Url String uri);

    @POST
    Call<JsonObject> getKomnetar(@Url String url, @Body ModelRequestInfo json);


    @POST
    Call<JsonObject> getListWhosLike(@Url String url, @Query("infoid") String infoid, @Query("mitraid") String mitraid);

    /*
    @FormUrlEncoded
    @POST("android/v2/index.php/microz/create_topik_chat")
    Call<JsonObject> create_topik_chat(@FieldMap Map<String,String> params);
    */

    @Multipart
    @POST
    Call<JsonObject> create_topik_chat(@Url String url, @PartMap Map<String, RequestBody> params, @Part MultipartBody.Part image);

    @Multipart
    @POST
    Call<JsonObject> create_topik_chat(@Url String url, @PartMap Map<String, RequestBody> params);

    @FormUrlEncoded
    @POST
    Call<JsonArray> getPriority(@Url String url, @FieldMap Map<String,String> params);

    @FormUrlEncoded
    @POST
    Call<JsonObject> create_chat_by_topik(@Url String url,  @FieldMap Map<String,String> params);


    @POST
    Call<JsonObject> postDefault(@Url String url, @Body ModelRequestInfo json);

    @POST
    Call<PojoDefault> postPojoDefault(@Url String url, @Body ModelRequestInfo json);

    @GET
    Call<List<Cities>> listCities(@Url String url);

    @GET
    Call<Integer> updateLastidInfo(@Url String url, @Query("localCount") Integer query, @Query("mitraid") String mitraid);

    @GET
    Call<List<Cities>> listCitiesOther(@Url String url);

    @GET
    Call<List<mProfesi>> listProfesi(@Url String url);

    @GET
    Call<List<mUsaha>> listUsaha(@Url String url);



    @FormUrlEncoded
    @POST
    Call<JsonObject> updateProfil(@Url String url, @Field("hp") String hp,  @Field("nama") String nama, @Field("alamat") String alamat, @Field("tgllahir") String tgllahir,@Field("gender") String gender);

    @Multipart
    @POST
    Call<JsonObject> updateProfileFoto(@Url String url, @Part("hp") RequestBody hp, @Part MultipartBody.Part image);

    @Multipart
    @POST
    Call<JsonObject> addIconTopikChat(@Url String url, @Part("hp") RequestBody hp, @Part MultipartBody.Part image);

    @POST
    Call<JsonObject> createProfesi(@Url String url, @Body ProfesiParamModel json);

    @Multipart
    @POST
    Call<JsonObject> registerFCM(@Url String url, @Path("crud") String crud, @Part("gcmid") RequestBody gcmid, @Part("mitraid") RequestBody mitraid);

    @FormUrlEncoded
    @POST
    Call<JsonObject> registerFcm(@Url String url, @Field("deviceid") String deviceid, @Field("fcmid")String fcmid, @Field("hp")String hp, @Field("mitraid")String mitraid);

    @Multipart
    @POST
    Call<PojoDefault> daftarMember(@Url String url
            , @Part MultipartBody.Part file
            , @Part("member_id") RequestBody member_id
            , @Part("email") RequestBody email
            , @Part("agenid") RequestBody agenid
            , @Part("hp") RequestBody hp
            , @Part("nama") RequestBody nama
            , @Part("alamat") RequestBody alamat
            , @Part("lat") RequestBody lat
            , @Part("lon") RequestBody lon
            , @Part("gender") RequestBody gender
            , @Part("kota") RequestBody kota
            , @Part("pertanyaan") RequestBody pertanyaan
            , @Part("jawaban") RequestBody jawaban
            , @Part("reg_gcm") RequestBody reg_gcm
            , @Part("mitraid") RequestBody mitraid
            , @Part("type") RequestBody type
    );


    @Multipart
    @POST
    Call<PojoDefault> tambahUsahaWithImage(@Url String uri
            , @Part MultipartBody.Part[] uploaded_file
            , @Part("nama") RequestBody nama
            , @Part("alamat") RequestBody alamat
            , @Part("bidang") RequestBody bidang
            , @Part("description") RequestBody description
            , @Part("url") RequestBody url
            , @Part("posisi") RequestBody posisi
            , @Part("lat") RequestBody lat
            , @Part("lon") RequestBody lon
            , @Part("type") RequestBody type
    );



    @Multipart
    @POST
    Call<PojoProfilUsaha> uploadInfoWithoutImage(
            @Url String url,
            @Part("mitraid") RequestBody mitraid,
            @Part("description") RequestBody description,
            @Part("hp") RequestBody hp,
            @Part("alamat") RequestBody alamat,
            @Part("lat") RequestBody lat,
            @Part("lon") RequestBody lon,
            @Part("status") RequestBody status,
            @Part("membergroup") RequestBody membergroup
    );

    @POST
    Call<JsonObject> exportContact(@Url String url, @Body List<phoneContactObject> json);

    @POST
    Call<JsonObject> retreiveContact(@Url String url);
}
