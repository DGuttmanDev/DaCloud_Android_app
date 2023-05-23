package es.pfc.dacloud.business.adapter.home;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import es.pfc.dacloud.R;
import es.pfc.dacloud.business.dto.ArchivoDTO;

public class PreviewAdapter extends BaseAdapter {
    private Context context;
    private List<ArchivoDTO> archivos;

    private ArchivoDTO archivo;

    public PreviewAdapter(Context context, List<ArchivoDTO> archivos) {
        this.context = context;
        this.archivos = archivos;
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
            showPopupMenu(opcionesButton);
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

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.inflate(R.menu.file_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.eliminar_archivo){
                eliminarArchivo(archivo.getIdArchivo());
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

    private void eliminarArchivo(Long id){

    }

}

