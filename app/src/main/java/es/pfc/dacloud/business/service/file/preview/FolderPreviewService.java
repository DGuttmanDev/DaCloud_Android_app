package es.pfc.dacloud.business.service.file.preview;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.HomePageActivity;
import es.pfc.dacloud.business.dto.ArchivoDTO;
import es.pfc.dacloud.business.task.preview.FolderPreviewTask;

public class FolderPreviewService {

    private FolderPreviewTask folderPreviewTask;
    private Context context;

    private List<ArchivoDTO> listaArchivosDTO;

    public FolderPreviewService(Context context, Long id) {
        this.context = context;
        folderPreviewTask = new FolderPreviewTask(context, id);
    }

    public List<ArchivoDTO> getPreview() throws ExecutionException, InterruptedException {

        folderPreviewTask.execute();
        boolean status = folderPreviewTask.get();
        String responseBody = folderPreviewTask.getResponseBody();
        Gson gson = new Gson();
        Type tipoListaArchivoDTO = new TypeToken<List<ArchivoDTO>>() {}.getType();
        this.listaArchivosDTO = gson.fromJson(responseBody, tipoListaArchivoDTO);

        return listaArchivosDTO;

    }

}
