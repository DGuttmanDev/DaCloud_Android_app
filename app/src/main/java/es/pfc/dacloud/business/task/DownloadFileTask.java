package es.pfc.dacloud.business.task;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.gson.Gson;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import es.pfc.dacloud.business.dto.ArchivoDTO;
import es.pfc.dacloud.business.dto.DescargaDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadFileTask extends AsyncTask<Long, Void, DescargaDTO >{

    private static final String API_URL = "http://192.168.0.19:8080/api/file/download/mobile";

    private static final int REQUEST_CODE_OPEN_DIRECTORY = 1;
    private SharedPreferences preferences;

    private Context context;
    private File file;

    public DownloadFileTask(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
    }

    @Override
    protected DescargaDTO doInBackground(Long... id) {

        String token = preferences.getString("token", null);

        OkHttpClient client = new OkHttpClient();

        String urlPeticion = API_URL + "?id=" + id[0];

        Request request = new Request.Builder()
                .url(urlPeticion)
                .header("token", token)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Gson gson = new Gson();
                String responseBody = response.body().string();
                DescargaDTO descargaDTO = gson.fromJson(responseBody, DescargaDTO.class);

                return descargaDTO;
            } else {
                // Manejar la respuesta no exitosa
                Log.e("DownloadFileTask", "Error en la respuesta HTTP: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void onPostExecute(DescargaDTO descargaDTO) {
        // Aquí puedes realizar acciones con el objeto descargaDTO, como guardar el archivo en el dispositivo
        if (descargaDTO != null) {
            // Obtener los datos del DTO
            Long id = descargaDTO.getId();
            String nombre = descargaDTO.getNombre();
            String base64Bytes = descargaDTO.getBase64Bytes();

            // Convertir el string Base64 a bytes
            byte[] byteArray = Base64.getDecoder().decode(base64Bytes);

            // Guardar el archivo en el dispositivo
            // Aquí puedes implementar la lógica para guardar el archivo con el nombre y bytes obtenidos

            // Ejemplo de cómo guardar el archivo en el almacenamiento externo
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // Obtener la ruta del directorio de descargas
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloadsDir.exists()) {
                    Log.d("Error directorio descargas", "No hay acceso a descargas");
                }

                String filePath = downloadsDir.getAbsolutePath() + "/" + nombre;
                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    outputStream.write(byteArray);
                    Toast.makeText(context, "Se ha descargado correctamente el archivo en Descargas", Toast.LENGTH_SHORT).show();
                    Log.d("DownloadFileTask", "Archivo guardado en: " + filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("DownloadFileTask", "No se pudo acceder al almacenamiento externo");
            }
        } else {
            // Manejar el caso en el que descargaDTO sea nulo
            Log.e("DownloadFileTask", "La respuesta fue nula");
        }

    }

}
