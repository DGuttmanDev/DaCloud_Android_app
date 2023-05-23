package es.pfc.dacloud.business.adapter.home;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.R;
import es.pfc.dacloud.business.dto.ArchivoDTO;
import es.pfc.dacloud.business.service.file.delete.DeleteTask;
import es.pfc.dacloud.business.service.file.preview.PreviewService;
import es.pfc.dacloud.business.service.file.preview.PreviewTask;
import es.pfc.dacloud.business.service.file.upload.UploadFileService;

public class PreviewAdapter extends BaseAdapter {
    private Context context;
    private PreviewService previewService;
    private List<ArchivoDTO> listaArchivosDto;
    private List<ArchivoDTO> archivos;

    private ArchivoDTO archivo;
    private GridView gridView;

    public PreviewAdapter(Context context, List<ArchivoDTO> archivos, GridView gridView) {
        this.context = context;
        this.archivos = archivos;
        this.gridView = gridView;
    }

    @Override
    public int getCount() {
        return archivos.size();
    }

    @Override
    public Object getItem(int position) {
        return archivos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        archivo = archivos.get(position);

        TextView textView = convertView.findViewById(R.id.text_view);
        textView.setText(archivo.getNombreArchivo());

        Long id = archivo.getIdArchivo();

        ImageView imageView = convertView.findViewById(R.id.image_view);

        String extension = getExtension(archivo.getNombreArchivo()).toUpperCase();


        switch (extension) {
            case "PNG", "JPG", "JPEG", "GIF", "HEIC":
                imageView.setImageResource(R.drawable.image_icon_purple);
                break;
            case "PDF", "ODT", "DOC", "DOCX", "TXT", "RTF", "XLS", "XLSX", "CSV", "PPT", "PPTX":
                imageView.setImageResource(R.drawable.document_icon_purple);
                break;
            case "EXE", "MSI", "MSP", "BAT", "CMD", "INF", "INS", "ISU", "JOB", "VB", "VBS", "PS1":
                imageView.setImageResource(R.drawable.instalador_icon_purple);
                break;
            default:
                imageView.setImageResource(R.drawable.extension_desconocida_icon_purple);
                break;

        }

        ImageView opcionesButton = convertView.findViewById(R.id.opciones_button);
        opcionesButton.setOnClickListener(view -> {
            showPopupMenu(opcionesButton, id);
        });

        return convertView;
    }

    private String getExtension(String fileName) {
        int indicePunto = fileName.lastIndexOf(".");

        if (indicePunto >= 0 && indicePunto < fileName.length() - 1) {
            return fileName.substring(indicePunto + 1);
        } else {
            return "";
        }

    }

    private void showPopupMenu(View anchor, Long id) {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.inflate(R.menu.file_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.eliminar_archivo){
                DeleteTask deleteTask = new DeleteTask(id, context);
                deleteTask.execute();
                try {
                    obtenerListaPreview();
                    gridView.setAdapter(new PreviewAdapter(context, listaArchivosDto, gridView));
                    gridView.setNumColumns(2);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            } else if (itemId == R.id.renombrar_archivo) {

                return true;
            } else {
                return false;
            }
        });

        // Muestra el menú emergente
        popupMenu.show();
    }

    private void obtenerListaPreview() throws ExecutionException, InterruptedException {
        previewService = new PreviewService(context);
        listaArchivosDto = previewService.getPreview();
        PreviewTask previewTask = new PreviewTask(context);
        previewTask.execute();
        boolean stauts = previewTask.get();
    }

}

