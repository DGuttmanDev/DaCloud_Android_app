package es.pfc.dacloud.business.service.file;

import android.content.Context;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.business.adapter.home.PreviewAdapter;
import es.pfc.dacloud.business.dto.ArchivoDTO;

public class RefreshGridService {

    public void actualizarGrid(List<ArchivoDTO> listaActualizada, GridView gridView, Context context, TextView cabeceraDirectorio){
        gridView.setAdapter(new PreviewAdapter(context, listaActualizada, gridView, cabeceraDirectorio));
        gridView.setNumColumns(2);
    }

}
