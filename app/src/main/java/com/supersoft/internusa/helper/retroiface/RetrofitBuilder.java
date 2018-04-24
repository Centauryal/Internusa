package com.supersoft.internusa.helper.retroiface;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.supersoft.internusa.helper.util.Constant;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by itclub21 on 10/30/2016.
 */
public class RetrofitBuilder {

    OkHttpClient client;
    String URL;
    public RetrofitBuilder(){
        this.URL = Constant.BASE_URL;
        client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor("MD5"))
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }


    public RetrofitBuilder(String action){
        this.URL = Constant.BASE_URL;
        client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(action))
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public RetrofitBuilder(String uri, String action){
        this.URL = uri;
        client = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor(action))
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    public Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(this.URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }

}
