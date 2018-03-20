package com.harunkor.simpledownload;

/**
 * Created by harunkor on 21.03.2018.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.appunite.appunitevideoplayer.PlayerActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static android.content.Context.NOTIFICATION_SERVICE;




 class SimpleDownload extends AsyncTask<String, String, String> {

    ProgressDialog barProgressDialog;
    String path;
    Context cnt;
    String turl;
    String foldername;


    public SimpleDownload(Context cnt,String foldername) {
        this.cnt = cnt;
        this.foldername=foldername;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        barProgressDialog = new ProgressDialog(cnt);
        barProgressDialog.setTitle("Downloading Video...");
        barProgressDialog.setMessage("Download in progress ...");
        barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
        barProgressDialog.setProgress(0);
        barProgressDialog.setMax(100);
        barProgressDialog.show();
        barProgressDialog.setCancelable(false);


    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;
        turl=aurl[0];

        try {
            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();

            conexion.connect();

            int lenghtOfFile = conexion.getContentLength();


            InputStream input = new BufferedInputStream(url.openStream());

            Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

            OutputStream output = null;
            if (isSDPresent) {

                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + foldername);


                if (!folder.exists()) {
                    folder.mkdir();
                }


                path = Environment.getExternalStorageDirectory() +
                        File.separator + foldername + aurl[1];


                output = new FileOutputStream(path);


            } else {

                File folder = new File(cnt.getFilesDir() +
                        File.separator + foldername + aurl[1]);


                if (!folder.exists()) {
                    folder.mkdir();
                }

                path = cnt.getFilesDir() +
                        File.separator + foldername + aurl[1];


                output = new FileOutputStream(path);


            }


            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;

                barProgressDialog.setProgress((int) ((total * 100) / lenghtOfFile));


                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
        }
        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        barProgressDialog.dismiss();
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


         Intent intentnatif = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        

        // intentnatif.setDataAndType(Uri.parse("file://" + path), "video/mp4");


        PendingIntent intent = PendingIntent.getActivity(cnt, 0,
                intentnatif, 0);

        Notification mNotification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mNotification = new Notification.Builder(cnt)

                    .setContentTitle("Successful Download !")
                    .setContentText("Download directory " + foldername + ". Click to open!")
                    .setSmallIcon(android.R.drawable.ic_menu_upload)
                    .setContentIntent(intent)
                    .setSound(soundUri)
                    .build();
            NotificationManager notificationManager = (NotificationManager) cnt.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, mNotification);




        }


    }
}