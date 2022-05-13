package com.example.foodappserver.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGeoCordinates {

    @GET("maps/api/geocode/json")
    Call<String> getGeoCode(@Query("address") String address);

    @GET("map/api/directions/json")
    Call<String> getDirections(@Query("origin") String origin,@Query("destination" ) String destination);

}
