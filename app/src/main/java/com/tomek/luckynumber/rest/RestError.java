package com.tomek.luckynumber.rest;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by tomek on 29.07.15.
 */
@Parcel
public class RestError {

    public RestError() {
    }



    @SerializedName("error_message")
    private String strMessage;

    public RestError(String strMessage)
    {
        this.strMessage = strMessage;
    }

    //Getters and setters
}
