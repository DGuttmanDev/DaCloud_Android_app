package es.pfc.dacloud.business.service.file.upload;

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

public class UploadFileTask extends AsyncTask<File, Void, Boolean> {
    private static final String API_URL = ConfigUtil.URL+"/file/upload";
    private List<MultipartFile> listaMultipartFile;
    private SharedPreferences preferences;
    private Long idDirectorioPadre;

    public UploadFileTask(List<MultipartFile> listaMultipartFile, Context context, Long idDirectorioPadre) {
        this.listaMultipartFile = listaMultipartFile;
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

            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("dir_id", idDirectorioPadre.toString());

            for (MultipartFile file : listaMultipartFile) {
                MediaType MEDIA_TYPE = MediaType.parse(file.getContentType());
                RequestBody requestBody = RequestBody.create(file.getBytes(), MEDIA_TYPE);
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", file.getOriginalFilename(), requestBody);
                multipartBuilder.addPart(filePart);
            }

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("token", token)
                    .post(multipartBuilder.build())
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
