package es.pfc.dacloud.business.task.newfolder;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.pfc.dacloud.business.config.ConfigUtil;
import es.pfc.dacloud.business.dto.ArchivoDTO;
import es.pfc.dacloud.business.dto.NewFolderDTO;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewFolderTask extends AsyncTask<File, Void, Boolean> {
    private static final String API_URL = ConfigUtil.URL+"/file/new/folder";
    private NewFolderDTO newFolderDTO;
    private Context context;
    private SharedPreferences preferences;

    public NewFolderTask(NewFolderDTO newFolderDTO, Context context) {
        this.newFolderDTO = newFolderDTO;
        this.context = context;
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
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

            MediaType JSON = MediaType.parse("application/json; charset=utf-8");

            Gson gson = new Gson();
            String jsonBody = gson.toJson(newFolderDTO);

            RequestBody requestBody = RequestBody.create(jsonBody, JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("token", token)
                    .post(requestBody)
                    .build();

// Enviar la solicitud al servidor
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
