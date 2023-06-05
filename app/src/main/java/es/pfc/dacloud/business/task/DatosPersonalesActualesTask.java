package es.pfc.dacloud.business.task;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.pfc.dacloud.business.config.ConfigUtil;
import es.pfc.dacloud.business.dto.DatosDTO;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DatosPersonalesActualesTask  extends AsyncTask<Void, Void, DatosDTO> {

    private static final String API_URL = ConfigUtil.URL+"/session/datos/actual";
    private SharedPreferences preferences;

    public DatosPersonalesActualesTask(Context context){
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
    }

    @Override
    protected DatosDTO doInBackground(Void... voids) {

        String token = preferences.getString("token", null);

        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            // Construir la solicitud con el encabezado de autenticación y el cuerpo
            Request request = new Request.Builder()
                    .url(API_URL)
                    .get()
                    .header("token", token)
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // Obtener el cuerpo de la respuesta como una cadena JSON
                String responseBody = response.body().string();

                Gson gson = new GsonBuilder().create();

                // Convertir la cadena JSON a un objeto DatosDTO utilizando Gson
                DatosDTO datosDTO = gson.fromJson(responseBody, DatosDTO.class);

                return datosDTO;
            } else {
                // Manejar la respuesta de error
                throw new IOException("Error en la respuesta: " + response.code());
            }


        } catch (Exception exception) {
            // Manejar cualquier excepción ocurrida durante la ejecución de la solicitud
            exception.printStackTrace();
            return null;
        }
    }

}