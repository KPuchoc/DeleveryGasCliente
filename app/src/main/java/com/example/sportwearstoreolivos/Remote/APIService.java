package com.example.sportwearstoreolivos.Remote;

import com.example.sportwearstoreolivos.Model.MyResponse;
import com.example.sportwearstoreolivos.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=BByqtk8-5L_yezaGIOY3VEqBdFCausesJqqjzadHTo9PcE-EZTLUhYovOVxs2_gkmXbZEFz4SE9j-n1dkQr7blY"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
