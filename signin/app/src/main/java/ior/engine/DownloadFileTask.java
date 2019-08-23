package ior.engine;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFileTask extends AsyncTask<String, Integer, String> {

    public static File file;
    Runnable onFinish;
    String url;
    Context mContext;
    ProgressDialog mProgressDialog;
    String fileName;


    public DownloadFileTask(Context activity, String url, String fileName, Runnable onFinish) {
        this.onFinish = onFinish;
        this.url = url;
        this.mContext = activity;
        this.fileName = fileName;

        mProgressDialog = new ProgressDialog(activity);

        mProgressDialog.setMessage("Downloading File...");
        mProgressDialog.setMessage(fileName);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        mProgressDialog.show();
    }

    protected String doInBackground() throws IOException {


        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(this.url);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            file = new File(path, fileName);
            output = new FileOutputStream(file);
            input = connection.getInputStream();
            //output = new FileOutputStream("/sdcard/aaaa.pdf");

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                     publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;


//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        file = new File(path, "DemoPicture.pdf");
//        try {
//            // Make sure the Pictures directory exists.
//            path.mkdirs();
//
//            URL url = new URL(this.url);
//            /* Open a connection to that URL. */
//            URLConnection ucon = url.openConnection();
//
//            /*
//             * Define InputStreams to read from the URLConnection.
//             */
//            is = ucon.getInputStream();
//
//            OutputStream os = new FileOutputStream(file);
//            byte[] data = new byte[is.available()];
//            is.read(data);
//            os.write(data);
//            is.close();
//            os.close();
//
//        } catch (IOException e) {
//            Log.d("ImageManager", "Error: " + e);
//        }
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            doInBackground();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
    }



    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mProgressDialog.dismiss();
        onFinish.run();
    }



//    protected void onPostExecute() {
//        // Tell the media scanner about the new file so that it is
//        // immediately available to the user.
//        MediaScannerConnection.scanFile(null,
//                new String[]{file.toString()}, null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.i("ExternalStorage", "Scanned " + path + ":");
//                        Log.i("ExternalStorage", "-> uri=" + uri);
//                    }
//                });
//
//
//
//    }
}
