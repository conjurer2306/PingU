package com.example.pingu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

public class InternetSpeedTest
        extends AsyncTask<String, Void, String> {

    long startTime;
    long endTime;
    private long takenTime;
    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse delegate = null;
    public InternetSpeedTest(AsyncResponse delegate){
        this.delegate = (AsyncResponse) delegate;
    }

    @Override
    protected String doInBackground(String... paramVarArgs) {

        startTime = System.currentTimeMillis();
        Log.d(TAG, "doInBackground: StartTime" + startTime);


        Bitmap bmp = null;
        try {
            URL ulrn = new URL(paramVarArgs[0]);
            HttpURLConnection con = (HttpURLConnection) ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);

            Bitmap bitmap = bmp;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 99, stream);
            byte[] imageInByte = stream.toByteArray();
            long lengthbmp = imageInByte.length;

            if (null != bmp) {
                endTime = System.currentTimeMillis();
                Log.d(TAG, "doInBackground: EndTIme" + endTime);
                return lengthbmp + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    protected void onPostExecute(String result) {

        if (result != null) {
            long dataSize = Integer.parseInt(result) / 1024;
            takenTime = endTime - startTime;
            double s = (double) takenTime / 1000;
            double speed = dataSize / s;
            delegate.processFinish(new DecimalFormat("##.##").format(speed));
            Log.d("SPEEDCHECK", "onPostExecute: " + "" + new DecimalFormat("##.##").format(speed) + "kb/second");
        }
    }
}