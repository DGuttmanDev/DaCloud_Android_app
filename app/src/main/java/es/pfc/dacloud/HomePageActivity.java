package es.pfc.dacloud;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.business.adapter.home.PreviewAdapter;
import es.pfc.dacloud.business.dto.ArchivoDTO;
import es.pfc.dacloud.business.service.file.picker.FilePickerService;
import es.pfc.dacloud.business.service.file.picker.FileUtils;
import es.pfc.dacloud.business.service.file.preview.PreviewService;
import es.pfc.dacloud.business.service.file.preview.PreviewTask;
import es.pfc.dacloud.business.service.file.upload.UploadFileService;

public class HomePageActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;

    private UploadFileService uploadFileService;
    private File file;
    private File tempFile;
    private FilePickerService filePickerService;
    private PreviewService previewService;
    private SharedPreferences preferences;
    private List<ArchivoDTO> listaArchivosDto;


    // ELEMENTOS INTERFAZ
    private FloatingActionButton fab;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_home_page);

        preferences = getSharedPreferences("AuthPreferences", MODE_PRIVATE);

        filePickerService = new FilePickerService(this);


        fab = findViewById(R.id.addFileFab);
        fab.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showFilePickerDialog();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CODE);
            }

        });

        gridView = findViewById(R.id.homeGrid);

        try {
            cargarPreview();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
                        obtenerListaPreview();
                        gridView.setAdapter(new PreviewAdapter(this, listaArchivosDto, gridView));
                        gridView.setNumColumns(2);
                    } catch (Exception exception) {
                        Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void obtenerListaPreview() throws ExecutionException, InterruptedException {
        previewService = new PreviewService(this);
        listaArchivosDto = previewService.getPreview();
        PreviewTask previewTask = new PreviewTask(this);
        previewTask.execute();
        boolean stauts = previewTask.get();
    }

    private void cargarPreview() throws ExecutionException, InterruptedException {
        obtenerListaPreview();
        gridView.setAdapter(new PreviewAdapter(this, listaArchivosDto, gridView));
        gridView.setNumColumns(2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Opciones", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_logout){
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("token");
            editor.apply();
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}