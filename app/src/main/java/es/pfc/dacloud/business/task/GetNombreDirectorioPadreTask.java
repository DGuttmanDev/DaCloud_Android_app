package es.pfc.dacloud.business.task;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.pfc.dacloud.business.config.ConfigUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class GetNombreDirectorioPadreTask extends AsyncTask<File, Void, Boolean> {
    private static final String API_URL = ConfigUtil.URL+"/file/nombre_directorio";
    private SharedPreferences preferences;
    private Long idDirectorioPadre;
    private String nombreDirectorio;

    public GetNombreDirectorioPadreTask(Context context, Long idDirectorioPadre) {
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
        this.idDirectorioPadre = idDirectorioPadre;
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
                    .url(API_URL+"?id="+idDirectorioPadre)
                    .header("token", token)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    return false;
                }

                nombreDirectorio = response.body().string();
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    public String getNombreDirectorio() {
        return nombreDirectorio;
    }
}
