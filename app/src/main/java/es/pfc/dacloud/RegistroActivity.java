package es.pfc.dacloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.pfc.dacloud.business.dto.RegistroDTO;
import es.pfc.dacloud.business.service.session.RegisterService;

public class RegistroActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private EditText nombreEditText;
    private EditText apellidosEditText;
    private EditText nickEditText;
    private EditText mailEditText;
    private EditText passwordEditText;
    private Button registroButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        preferences = getSharedPreferences("AuthPreferences", MODE_PRIVATE);


        nombreEditText = findViewById(R.id.nombreRegistroED);
        apellidosEditText = findViewById(R.id.apellidosRegistroED);
        nickEditText = findViewById(R.id.nickRegistroED);
        mailEditText = findViewById(R.id.emailRegistroET);
        passwordEditText = findViewById(R.id.passwordRegistroET);
        registroButton = findViewById(R.id.registroButton);

        registroButton.setOnClickListener(view -> {
            try {
                registro();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void registro() throws InterruptedException {
        String nombre = nombreEditText.getText().toString();
        String apellidos = apellidosEditText.getText().toString();
        String nick = nickEditText.getText().toString();
        String email = mailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String correo = email.trim();
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.(com|es)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);

        if (matcher.matches()) {

            if (password.length() >= 8){
                RegistroDTO registroDTO = new RegistroDTO(nombre, apellidos, password, nick, email);
                RegisterService registerService = new RegisterService(registroDTO, this, this);
                Thread hilo = new Thread(registerService);
                hilo.start();
                hilo.join();
                if (preferences.contains("token")){
                    setResult(RESULT_OK);
                    this.finish();
                } else {
                    Toast.makeText(this, "Error login", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "La contraseña introducida es demasiado sencilla. Por favor introduce una contraseña que contenga como mínimo 8 carácteres.", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "El correo introducido no es válido.", Toast.LENGTH_SHORT).show();
        }

    }
}