package com.weblogic.lutskbus;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public enum AppHttpClient {
    INSTANCE;

    private HttpClient configuredHttpClient = null;

    public HttpClient getConfiguredHttpClient() {
        if (configuredHttpClient == null) {
            try {
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params, 5000);
                HttpConnectionParams.setSoTimeout(params, 5000);
                configuredHttpClient = new DefaultHttpClient(params);

                HttpGet httpGetguest = new HttpGet("http://mak.lutsk.ua/guest");

                configuredHttpClient.execute(httpGetguest);
            } catch (Exception e) {
                configuredHttpClient = new DefaultHttpClient();
            }


        }

        return configuredHttpClient;
    }
}
