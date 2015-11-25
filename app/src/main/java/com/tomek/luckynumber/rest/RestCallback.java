package com.tomek.luckynumber.rest;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by tomek on 29.07.15.
 */
public abstract class RestCallback<String> implements Callback<String>
{
    public abstract void failure(RestError restError);

    @Override
    public void failure(RetrofitError error)
    {
        RestError restError = (RestError) error.getBodyAs(RestError.class);

        if (restError != null)
            failure(restError);
        else
        {
            failure(new RestError(error.getMessage()));
        }
    }
}
