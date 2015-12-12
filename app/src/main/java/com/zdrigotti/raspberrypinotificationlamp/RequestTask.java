package com.zdrigotti.raspberrypinotificationlamp;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RequestTask extends AsyncTask<String, Void, String> {

    private String data;
    private String url;

    public RequestTask(String data, String url) {
        this.data = data;
        this.url = url;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        try {
            // Construct the request and send it
            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(data));
            response = httpclient.execute(request);

            // Read in the response if it's OK
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            }
            else {
                // Close the connection
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        }
        catch (ClientProtocolException e) {
            Log.i("RequestTask", "ClientProtocolException");
        }
        catch (IOException e) {
            Log.i("RequestTask", "IOException");
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result != null) {
            Log.i("RequestTask", result);
        }
        else {
            Log.i("RequestTask", "Null result");
        }
    }
}