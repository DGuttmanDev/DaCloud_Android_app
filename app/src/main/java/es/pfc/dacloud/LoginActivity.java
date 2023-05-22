package es.pfc.dacloud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import es.pfc.dacloud.business.dto.LoginDTO;
import es.pfc.dacloud.business.service.session.LoginService;
import es.pfc.dacloud.business.service.session.RegisterService;

public class LoginActivity extends AppCompatActivity {

    private static final int TOKEN_STATUS = 200;

    SharedPreferences preferences;
    EditText emailET;
    EditText passwordET;

    TextView crearCuenta;

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("AuthPreferences", MODE_PRIVATE);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        crearCuenta = findViewById(R.id.crearCuenta);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            try {
                login();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        crearCuenta.setOnClickListener(view -> {
            Intent registroIntent = new Intent(this, RegistroActivity.class);
            startActivityForResult(registroIntent, TOKEN_STATUS);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Log.d("Fin", "Ha finalizado correctamente el registro");
            setResult(RESULT_OK);
            this.finish();
        }

    }

    private void login() throws InterruptedException {
        String email = emailET.getText().toString();
        //String password = CryptUtil.encriptarContraseña(passwordET.getText().toString());
        String password = passwordET.getText().toString();
        LoginDTO loginDTO = new LoginDTO(email, password);

        LoginService loginService = new LoginService(loginDTO, this, this);
        Thread hilo = new Thread(loginService);
        hilo.start();
        hilo.join();

        if (preferences.contains("token")){
            setResult(RESULT_OK);
            this.finish();
        } else {
            Toast.makeText(this, "Error login", Toast.LENGTH_SHORT).show();
        }
    }

}