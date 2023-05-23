package es.pfc.dacloud.business.service.file.picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import es.pfc.dacloud.business.service.file.upload.UploadFileService;

public class FilePickerService extends AppCompatActivity {

    private Context context;
    private UploadFileService uploadFileService;
    private File file;
    private File tempFile;

    public FilePickerService(Context context) {
        this.context = context;
    }

    public void showFilePickerDialog() {
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("*/*");
        data = Intent.createChooser(data, "Selecciona un archivo");
        activityResultLauncher.launch(data);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(context, "hola", Toast.LENGTH_SHORT).show();
                    /*
                    Intent data = result.getData();
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    try {
                        file = new File(path);
                    } catch (Exception exception) {
                        Log.d("Error", "Error al abrir el archivo");
                    }
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        File tempDir = getCacheDir();
                        tempFile = new File(tempDir, file.getName());
                        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, length);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        uploadFileService = new UploadFileService(tempFile, this);
                        uploadFileService.enviarArchivo();
                    } catch (Exception exception) {
                        Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show();
                    }

                     */
                }
            });

}
