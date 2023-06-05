package es.pfc.dacloud.business.task.preview;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FolderPreviewTask extends AsyncTask<File, Void, Boolean> {

    private static final String API_URL = "http://192.168.0.19:8080/api/file/folder/preview";
    private SharedPreferences preferences;
    private static String responseBody;
    private static int responseCode;
    private Long id;

    public FolderPreviewTask(Context context, Long id) {
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
        this.id = id;
    }

    @Override
    protected Boolean doInBackground(File... files) {

        String token = preferences.getString("token", null);

        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url(API_URL+"?dir_id="+id)
                    .header("token", token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    responseBody = response.body().string();
                    responseCode = response.code();
                    return false;
                }
                // Devolver el cuerpo de la respuesta como un String
                responseBody = response.body().string();
                responseCode = response.code();
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getResponseBody(){
        return responseBody;
    }

    public int getResponseCode(){
        return responseCode;
    }

}
