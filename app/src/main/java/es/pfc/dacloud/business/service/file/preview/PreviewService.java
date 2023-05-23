package es.pfc.dacloud.business.service.file.preview;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.business.dto.ArchivoDTO;

public class PreviewService {

    private PreviewTask previewTask;

    private Context context;

    private List<ArchivoDTO> listaArchivosDTO;

    public PreviewService(Context context) {
        this.context = context;
        previewTask = new PreviewTask(context);
    }

    public List<ArchivoDTO> getPreview() throws ExecutionException, InterruptedException {

        previewTask.execute();
        boolean status = previewTask.get();
        String responseBody = previewTask.getResponseBody();
        Gson gson = new Gson();
        Type tipoListaArchivoDTO = new TypeToken<List<ArchivoDTO>>() {}.getType();
        this.listaArchivosDTO = gson.fromJson(responseBody, tipoListaArchivoDTO);

        return listaArchivosDTO;

    }

}
