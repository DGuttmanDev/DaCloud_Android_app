package es.pfc.dacloud;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;

import es.pfc.dacloud.business.service.file.picker.FileUtils;
import es.pfc.dacloud.business.service.file.upload.UploadFileService;

public class HomePageActivity extends AppCompatActivity {

    private static final int REQUEST_CODE  = 1;
    private UploadFileService uploadFileService;
    private File file;

    private SharedPreferences preferences;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
        setContentView(R.layout.activity_home_page);
        preferences = getSharedPreferences("AuthPreferences", MODE_PRIVATE);

        fab = findViewById(R.id.addFileFab);
        fab.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(HomePageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                showFilePickerDialog();
            } else {
                requestStoragePermission();
            }

        });
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }
    private void showFilePickerDialog() {
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
                    String path = FileUtils.getPath(this,uri);
                    Log.d("Path", path);
                    try {
                        file = new File(path);
                    } catch (Exception exception){
                        Log.d("Error", "Error al abrir el archivo");
                    }
                    uploadFileService = new UploadFileService(file, this);
                    try {
                        file = new File(uri.getPath());
                        uploadFileService = new UploadFileService(file, this);
                        uploadFileService.enviarArchivo();
                    } catch (Exception exception) {
                        Toast.makeText(this, "Error al abrir el archivo", Toast.LENGTH_SHORT).show();
                    }
                }
            });

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