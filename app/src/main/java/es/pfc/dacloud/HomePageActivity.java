package es.pfc.dacloud;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.TextView;
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
import es.pfc.dacloud.business.dto.NewFolderDTO;
import es.pfc.dacloud.business.service.file.RefreshGridService;
import es.pfc.dacloud.business.service.file.picker.FilePickerService;
import es.pfc.dacloud.business.service.file.picker.FileUtils;
import es.pfc.dacloud.business.service.file.preview.FolderPreviewService;
import es.pfc.dacloud.business.service.file.preview.PreviewService;
import es.pfc.dacloud.business.service.file.preview.PreviewTask;
import es.pfc.dacloud.business.service.file.upload.UploadFileService;
import es.pfc.dacloud.business.task.GetNombreDirectorioPadreTask;
import es.pfc.dacloud.business.task.newfolder.NewFolderTask;
import es.pfc.dacloud.business.task.preview.FolderPreviewTask;

public class HomePageActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;

    private static final int REQUEST_CODE_PERMISSIONS = 101;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private SharedPreferences preferences;

    private UploadFileService uploadFileService;
    public static Long idDirectorio;
    private File file;
    private File tempFile;
    private List<ArchivoDTO> listaArchivosDto;

    // ELEMENTOS INTERFAZ
    private FloatingActionButton fab;
    private GridView gridView;
    private TextView nombre;
    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        getSupportActionBar().setTitle("");

        gridView = findViewById(R.id.homeGrid);
        nombre = findViewById(R.id.nombreDirectorioTextView);

        idDirectorio = 0L;
        preferences = getSharedPreferences("AuthPreferences", MODE_PRIVATE);
        String nombreDirectorio = "";
        if (idDirectorio > 0L){
            GetNombreDirectorioPadreTask getNombreDirectorioPadreTask = new GetNombreDirectorioPadreTask(this, idDirectorio);
            getNombreDirectorioPadreTask.execute();
            try {
                getNombreDirectorioPadreTask.get(); // Esperar a que la tarea se complete
                nombreDirectorio = getNombreDirectorioPadreTask.getNombreDirectorio();
            } catch (Exception e) {
                // Manejar excepciones
            }
        } else {
            nombreDirectorio = "Directorio principal";
        }

        actualizarHeader(nombreDirectorio);

        fab = findViewById(R.id.addFileFab);
        fab.setOnClickListener(view -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!hasPermissions(REQUIRED_PERMISSIONS)) {
                    requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                }
            }

        });

        FolderPreviewService folderPreviewService = new FolderPreviewService(this, idDirectorio);
        List<ArchivoDTO> listaFolder = null;
        try {
            listaFolder = folderPreviewService.getPreview();
            RefreshGridService refreshGridService = new RefreshGridService();
            refreshGridService.actualizarGrid(listaFolder, gridView, this);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Se requieren permisos de lectura y escritura para acceder al almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        try {
            cargarPreview();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
                        cargarPreview();
                    } catch (Exception exception) {
                        Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void showPopupMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.inflate(R.menu.upload_select_menu);

        popupMenu.setOnMenuItemClickListener(item -> {
            int idItem = item.getItemId();
            if (idItem == R.id.subir_archivo) {
                showFilePickerDialog();
                return true;
            } else if (idItem == R.id.crear_directorio) {
                showNewFolderDialog();
                return true;
            } else {
                return false;
            }
        });
        popupMenu.show();
    }

    private void showNewFolderDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.rename_layout, null);

        EditText editTextNewName = dialogView.findViewById(R.id.editTextNewName);
        editTextNewName.setHint("Nombre de la carpeta");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Nueva carpeta");

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("Crear", (dialog, which) -> {
            String newName = editTextNewName.getText().toString().trim();
            NewFolderDTO newFolderDTO = new NewFolderDTO();
            newFolderDTO.setNombreDirectorio(newName);
            newFolderDTO.setIdDirectorioPadre(idDirectorio);
            try {
                crearDirectorio(newFolderDTO);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        // MOSTRAR DIALOGO
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void crearDirectorio(NewFolderDTO newFolderDTO) throws ExecutionException, InterruptedException {
        NewFolderTask newFolderTask = new NewFolderTask(newFolderDTO, this);
        newFolderTask.execute();
        FolderPreviewService folderPreviewService = new FolderPreviewService(this, idDirectorio);
        List<ArchivoDTO> listaFolder = folderPreviewService.getPreview();
        RefreshGridService refreshGridService = new RefreshGridService();
        refreshGridService.actualizarGrid(listaFolder, gridView, this);
    }

    private void obtenerListaPreview() throws ExecutionException, InterruptedException {
        FolderPreviewService previewService = new FolderPreviewService(this, idDirectorio);
        listaArchivosDto = previewService.getPreview();
        FolderPreviewTask previewTask = new FolderPreviewTask(this, idDirectorio);
        previewTask.execute();
    }

    private void cargarPreview() throws ExecutionException, InterruptedException {
        obtenerListaPreview();
        if (listaArchivosDto != null || listaArchivosDto.size() > 0){
            RefreshGridService refreshGridService = new RefreshGridService();
            refreshGridService.actualizarGrid(listaArchivosDto, gridView, this);
        }
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

        if (id == R.id.action_logout) {
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

    public void showFilePickerDialog() {
        Intent data = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        data.setType("*/*");
        data = Intent.createChooser(data, "Selecciona un archivo");
        activityResultLauncher.launch(data);
    }

    private void actualizarHeader(String nombreDirectorio){
        nombre.setText(nombreDirectorio);
    }

    public static void setIdDirectorioPadre(Long id){
        idDirectorio = id;
    }

    public static Long getIdDirectorio(){
        return idDirectorio;
    }
}