package com.felp64.routedecoder.geocoding;

public interface ReverseGeocodingClient {

    String resolveAddress(String latitude, String longitude);
}
