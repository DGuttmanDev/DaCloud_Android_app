package es.pfc.dacloud.business.service.file.upload;

import android.content.Context;
import android.util.Log;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;

public class UploadFileService {

    private static final String API_URL = "http://192.168.0.19:8080/api/file/upload";
    private static final int REQUEST_CODE = 100;
    private OkHttpClient client;

    private File file;
    private String fileName;
    private Context context;

    public UploadFileService(File file, Context context){
        this.file = file;
        this.fileName = file.getName();
        client = new OkHttpClient();
        this.context = context;
    }

    public void enviarArchivo() throws IOException {

        Log.d("Entro", "Entro a enviar archivo");

        MultipartFile archivo = crearMultipartFile(file);

        Log.d("archivo", file.getPath());

        /*
        try{
            UploadFileTask task = new UploadFileTask(archivo, context);
            task.execute();
        } catch (Exception exception){

        }

         */

    }

    public MultipartFile crearMultipartFile(File archivo) {
        try {
            FileInputStream fileInputStream = new FileInputStream(archivo);
            return new MockMultipartFile(
                    archivo.getName(),
                    archivo.getName(),
                    "application/octet-stream",
                    fileInputStream
            );
        } catch (IOException e) {
            Log.d("Error al crear MultipartFile", e.getMessage());
            return null;
        }

    }

}