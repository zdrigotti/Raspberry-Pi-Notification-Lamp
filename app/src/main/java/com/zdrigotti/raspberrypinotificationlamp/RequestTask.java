package com.zdrigotti.raspberrypinotificationlamp;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class RequestTask extends AsyncTask<String, Void, String> {

    private List<NameValuePair> data;
    private String url;

    public RequestTask(List<NameValuePair> data, String url) {
        this.data = data;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        try {
            HttpPost request = new HttpPost(url);

            request.setEntity(new UrlEncodedFormEntity(data));

            response = httpclient.execute(request);

            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            }
            else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }
        catch (ClientProtocolException e) {
            //TODO Handle problems..
        }
        catch (IOException e) {
            //TODO Handle problems..
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("RequestTask", result);
        //Do anything with response..
    }
}