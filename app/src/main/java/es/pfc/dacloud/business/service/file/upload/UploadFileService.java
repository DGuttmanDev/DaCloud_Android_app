package es.pfc.dacloud.business.service.file.upload;

import android.content.Context;
import android.util.Log;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class UploadFileService {

    private List<File> listaFile;
    private List<MultipartFile> listaMultipartFile;
    private Context context;

    public UploadFileService(File file, Context context){
        this.listaFile = new ArrayList<>();
        listaFile.add(file);
        this.context = context;
        this.listaMultipartFile = new ArrayList<>();
    }

    public void enviarArchivo() {

        for (File file: listaFile){
            listaMultipartFile.add(crearMultipartFile(file));
        }

        try{
            UploadFileTask task = new UploadFileTask(listaMultipartFile, context);
            task.execute();
        } catch (Exception exception){
            // FALTA ERROR
        }

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