package com.tomek.luckynumber.rest.api;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by tomek on 27.07.15.
 */
public interface ConnectionInterface {


    @GET("/a/add")
    void getLink(@Query("u") String originalAddress, Callback<String> callback);
    //convenient way to return String instead of JSON values (key, value)

}