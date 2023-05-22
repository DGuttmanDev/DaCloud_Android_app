package es.pfc.dacloud.business.service.file.upload;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadFileTask extends AsyncTask<File, Void, Boolean> {

    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "http://192.168.0.19:8080/api/file/upload/single";

    MultipartFile file;
    private Context context;

    public UploadFileTask(MultipartFile file, Context context) {
        this.file = file;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(File... files) {
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            MediaType MEDIA_TYPE = MediaType.parse("application/octet-stream");


            RequestBody requestBody = RequestBody.create(file.getBytes(), MEDIA_TYPE);

            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getOriginalFilename(), requestBody);


            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addPart(filePart) // Aquí se agrega el archivo como un MultipartBody.Part
                            .build())
                    .build();

// Enviar la solicitud al servidor
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected response code: " + response);
                }
                // Aquí se puede leer la respuesta del servidor si se desea
                String responseBody = response.body().string();
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
