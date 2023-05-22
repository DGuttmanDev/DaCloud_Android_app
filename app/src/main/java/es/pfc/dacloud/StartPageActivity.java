package es.pfc.dacloud;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class StartPageActivity extends AppCompatActivity {

    private static final int TOKEN_STATUS = 200;
    private SharedPreferences preferences;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        preferences = getSharedPreferences("AuthPreferences", MODE_PRIVATE);

        if (!preferences.contains("token")){
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(loginIntent, TOKEN_STATUS);
        } else {
            Intent homeIntent = new Intent(this, HomePageActivity.class);
            startActivity(homeIntent);
        }


        /*
        if (!StoragePermissionUtil.checkStoragePermission(this)){
            StoragePermissionUtil.requestPermissions(this);
        }
         */

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            this.recreate();
        } else if (resultCode == RESULT_CANCELED){
            finishAffinity();
        }

    }
}