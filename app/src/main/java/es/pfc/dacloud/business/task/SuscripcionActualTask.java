package es.pfc.dacloud.business.task;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import es.pfc.dacloud.business.config.ConfigUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SuscripcionActualTask extends AsyncTask<Void, Void, String> {

    private static final String API_URL = ConfigUtil.URL+"/session/suscripcion/actual";
    private SharedPreferences preferences;

    public SuscripcionActualTask(Context context){
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Void... voids) {

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

            // Ejecutar la solicitud
            Response response = client.newCall(request).execute();

            // Verificar si la solicitud fue exitosa (código de respuesta 200)
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                // Manejar el caso de respuesta no exitosa
                // Por ejemplo, puedes obtener el mensaje de error del cuerpo de la respuesta
                String errorResponse = response.body().string();
                Log.e("CambiarSuscripcionTask", "Error: " + errorResponse);
                return "";
            }
        } catch (Exception exception) {
            // Manejar cualquier excepción ocurrida durante la ejecución de la solicitud
            exception.printStackTrace();
            return "";
        }
    }

}
