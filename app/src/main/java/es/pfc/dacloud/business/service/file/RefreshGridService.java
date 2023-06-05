package es.pfc.dacloud.business.service.file;

import android.content.Context;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.List;
import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.business.adapter.home.PreviewAdapter;
import es.pfc.dacloud.business.dto.ArchivoDTO;

public class RefreshGridService {

    public void actualizarGrid(List<ArchivoDTO> listaActualizada, GridView gridView, Context context){
        gridView.setAdapter(new PreviewAdapter(context, listaActualizada, gridView));
        gridView.setNumColumns(2);
    }

}
