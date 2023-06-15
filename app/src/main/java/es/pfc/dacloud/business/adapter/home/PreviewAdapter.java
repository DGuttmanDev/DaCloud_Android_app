package es.pfc.dacloud.business.adapter.home;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.HomePageActivity;
import es.pfc.dacloud.R;
import es.pfc.dacloud.business.dto.ArchivoDTO;
import es.pfc.dacloud.business.service.file.RefreshGridService;
import es.pfc.dacloud.business.service.file.delete.DeleteTask;
import es.pfc.dacloud.business.service.file.preview.FolderPreviewService;
import es.pfc.dacloud.business.service.file.preview.PreviewService;
import es.pfc.dacloud.business.service.file.preview.PreviewTask;
import es.pfc.dacloud.business.service.file.rename.RenameTask;
import es.pfc.dacloud.business.task.DownloadFileTask;
import es.pfc.dacloud.business.task.GetNombreDirectorioPadreTask;

public class PreviewAdapter extends BaseAdapter {
    private Context context;
    private PreviewService previewService;
    private List<ArchivoDTO> listaArchivosDto;
    private List<ArchivoDTO> archivos;

    private ArchivoDTO archivo;
    private GridView gridView;

    private TextView textView;

    public PreviewAdapter(Context context, List<ArchivoDTO> archivos, GridView gridView, TextView cabeceraDirectorio) {
        this.context = context;
        this.archivos = archivos;
        this.gridView = gridView;
        this.textView = cabeceraDirectorio;
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
        String nombre = getNameWithoutExtension(archivo.getNombreArchivo());

        ImageView imageView = convertView.findViewById(R.id.image_view);

        String extension = getExtension(archivo.getNombreArchivo()).toUpperCase();

        if (archivo.isFolder()) {
            extension = "FOLDER";
            convertView.setOnClickListener(view -> {
                try {
                    Log.d("datos", String.valueOf(id));
                    accederDirectorio(archivo, id);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

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
            case "FOLDER":
                imageView.setImageResource(R.drawable.icon_folder_purple_light);
                break;
            default:
                imageView.setImageResource(R.drawable.extension_desconocida_icon_purple);
                break;

        }

        ImageView opcionesButton = convertView.findViewById(R.id.opciones_button);
        if (archivo.isFolder()){
            opcionesButton.setOnClickListener(view -> {
                showPopupMenu(opcionesButton, id, nombre, archivo.getNombreArchivo());
            });
        } else {
            opcionesButton.setOnClickListener(view -> {
                showPopupMenuArchivo(opcionesButton, id, nombre, archivo.getNombreArchivo());
            });
        }


        return convertView;
    }

    private void accederDirectorio(ArchivoDTO archivo, Long id) throws ExecutionException, InterruptedException {
        FolderPreviewService folderPreviewService = new FolderPreviewService(context, id);
        List<ArchivoDTO> listaActualizada = folderPreviewService.getPreview();
        RefreshGridService refreshGridService = new RefreshGridService();
        refreshGridService.actualizarGrid(listaActualizada, gridView, context, textView);
        HomePageActivity.idDirectorio = id;
        GetNombreDirectorioPadreTask getNombreDirectorioPadreTask = new GetNombreDirectorioPadreTask(context, id);
        getNombreDirectorioPadreTask.execute();
        String nombreDirectorio;
        try {
            getNombreDirectorioPadreTask.get();
            nombreDirectorio = getNombreDirectorioPadreTask.getNombreDirectorio();
            textView.setText(nombreDirectorio);
        } catch (Exception e) {
            // Manejar excepciones
        }

    }

    private String getExtension(String fileName) {
        int indicePunto = fileName.lastIndexOf(".");

        if (indicePunto >= 0 && indicePunto < fileName.length() - 1) {
            return fileName.substring(indicePunto + 1);
        } else {
            return "";
        }

    }

    private String getNameWithoutExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex >= 0) {
            return fileName.substring(0, lastIndex);
        } else {
            return fileName;
        }
    }

    private void showPopupMenuArchivo(View anchor, Long id, String nombre, String nombreAntiguo) {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.inflate(R.menu.file_menu_archivo);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.eliminar_archivo) {
                DeleteTask deleteTask = new DeleteTask(id, context);
                deleteTask.execute();
                FolderPreviewService folderPreviewService2 = new FolderPreviewService(context, HomePageActivity.getIdDirectorio());
                try {
                    List<ArchivoDTO> listaFolder2 = folderPreviewService2.getPreview();
                    RefreshGridService refreshGridService = new RefreshGridService();
                    refreshGridService.actualizarGrid(listaFolder2, gridView, context, textView);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            } else if (itemId == R.id.renombrar_archivo) {
                showRenameDialog(id, nombre, nombreAntiguo);
                return true;
            } else if (itemId == R.id.descargar_archivo){
                DownloadFileTask downloadFileTask = new DownloadFileTask(context);
                downloadFileTask.execute(id);
                //saveMultipartFileToDownloads(context, multipartFile);
                return false;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void showPopupMenu(View anchor, Long id, String nombre, String nombreAntiguo) {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.inflate(R.menu.file_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.eliminar_archivo) {
                DeleteTask deleteTask = new DeleteTask(id, context);
                deleteTask.execute();
                FolderPreviewService folderPreviewService2 = new FolderPreviewService(context, HomePageActivity.getIdDirectorio());
                try {
                    List<ArchivoDTO> listaFolder2 = folderPreviewService2.getPreview();
                    RefreshGridService refreshGridService = new RefreshGridService();
                    refreshGridService.actualizarGrid(listaFolder2, gridView, context, textView);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            } else if (itemId == R.id.renombrar_archivo) {
                showRenameDialog(id, nombre, nombreAntiguo);
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void showRenameDialog(Long id, String nombre, String nombreAntiguo) {
        // Inflar el diseño del diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.rename_layout, null);

        // Obtener referencias a los elementos del diálogo
        EditText editTextNewName = dialogView.findViewById(R.id.editTextNewName);
        editTextNewName.setText(nombre);

        // Crear el diálogo de alerta
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setTitle("Renombrar archivo");

        // Configurar el botón Cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        // Configurar el botón Aplicar
        builder.setPositiveButton("Aplicar", (dialog, which) -> {
            String newName = editTextNewName.getText().toString().trim();
            String extension = getExtension(nombreAntiguo);
            String nuevoNombreCompleto = newName + "." + extension;
            ArchivoDTO archivoDTO = new ArchivoDTO();
            archivoDTO.setIdArchivo(id);
            archivoDTO.setNombreArchivo(nuevoNombreCompleto);
            actualizar(archivoDTO);
            FolderPreviewService folderPreviewService = new FolderPreviewService(context, HomePageActivity.getIdDirectorio());
            List<ArchivoDTO> listaFolder = null;
            try {
                listaFolder = folderPreviewService.getPreview();
                RefreshGridService refreshGridService = new RefreshGridService();
                refreshGridService.actualizarGrid(listaFolder, gridView, context, textView);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // Mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void actualizar(ArchivoDTO archivoDTO) {
        RenameTask renameTask = new RenameTask(archivoDTO, context);
        renameTask.execute();
    }

    private void obtenerListaPreview() throws ExecutionException, InterruptedException {
        previewService = new PreviewService(context);
        listaArchivosDto = previewService.getPreview();
        PreviewTask previewTask = new PreviewTask(context);
        previewTask.execute();
        boolean stauts = previewTask.get();
    }

}

