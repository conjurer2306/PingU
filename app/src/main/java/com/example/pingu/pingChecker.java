package com.example.pingu;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;

public class pingChecker extends AsyncTask<Void, Void, String> {
    long timeDifference;

    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse delegate = null;
    public pingChecker(AsyncResponse delegate){
        this.delegate = delegate;
    }
    @Override
    protected String doInBackground(Void... params) {
        String host = "139.59.22.118";
        int timeout = 3000;
        long beforeTime = System.currentTimeMillis();
        try {
            boolean reachable = InetAddress.getByName(host).isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long afterTime = System.currentTimeMillis();
        timeDifference = afterTime - beforeTime;
        return String.valueOf(timeDifference)+" ms";
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
