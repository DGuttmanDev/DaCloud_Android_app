package es.pfc.dacloud.business.task;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.pfc.dacloud.business.config.ConfigUtil;
import es.pfc.dacloud.business.dto.DatosDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActualizarDatosTask extends AsyncTask<DatosDTO, Void, Boolean> {

    private static final String API_URL = ConfigUtil.URL + "/session/datos/actualizar";
    private SharedPreferences preferences;

    public ActualizarDatosTask(Context context) {
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
    }

    @Override
    protected Boolean doInBackground(DatosDTO... datosDTOS) {

        String token = preferences.getString("token", null);

        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            DatosDTO datosDTO = datosDTOS[0];

            Gson gson = new GsonBuilder().create();
            String requestBody = gson.toJson(datosDTO);

            RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBody);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .header("token", token)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            return false;
        }

    }

}