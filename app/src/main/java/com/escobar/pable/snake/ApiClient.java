package com.escobar.pable.snake;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Professor on 4/21/2017.
 */
public interface ApiClient {
    @Headers("Authorization: Bearer 60f6a9c5d4674ffb87c8221e19082eaa")
    @GET("https://api.api.ai/v1/query/")
    Call<ApiResponse> askApi(
            @Query("query") String query,
            @Query("v") String v,
            @Query("lang") String lang,
            @Query("sessionId") int sessionId
    );

}
