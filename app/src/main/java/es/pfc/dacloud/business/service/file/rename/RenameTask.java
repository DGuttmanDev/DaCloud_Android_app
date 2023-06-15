package es.pfc.dacloud.business.service.file.rename;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.pfc.dacloud.business.config.ConfigUtil;
import es.pfc.dacloud.business.dto.ArchivoDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RenameTask extends AsyncTask<File, Void, Boolean> {

    private static final String API_URL = ConfigUtil.URL+"/file/rename";
    private Context context;
    private SharedPreferences preferences;
    private ArchivoDTO archivoDTO;

    public RenameTask(ArchivoDTO archivoDTO, Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
        this.archivoDTO = archivoDTO;
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

            Gson gson = new GsonBuilder().create();
            String jsonBody = gson.toJson(archivoDTO);
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

            Request request = new Request.Builder()
                    .url(API_URL + "?id="+archivoDTO.getIdArchivo())
                    .header("token", token)
                    .post(requestBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }
                // Aquí se puede leer la respuesta del servidor si se desea
                String responseBody = response.body().string();
                Log.d("respuesta", responseBody);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
