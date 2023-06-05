package es.pfc.dacloud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.business.dto.DatosDTO;
import es.pfc.dacloud.business.task.ActualizarDatosTask;
import es.pfc.dacloud.business.task.DatosPersonalesActualesTask;

public class DatosPersonalesActivity extends AppCompatActivity {

    private EditText nombre;
    private EditText apellidos;

    private Button actualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);

        nombre = findViewById(R.id.nombreActualizarED);
        apellidos = findViewById(R.id.apellidosActualizarED);
        actualizar = findViewById(R.id.actualizar);

        DatosPersonalesActualesTask datosPersonalesActualesTask = new DatosPersonalesActualesTask(this);
        try {
            DatosDTO datosDTO = datosPersonalesActualesTask.execute().get();
            if (datosDTO != null){
                nombre.setText(datosDTO.getNombre());
                apellidos.setText(datosDTO.getApellidos());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        actualizar.setOnClickListener(view -> {
            ActualizarDatosTask actualizarDatosTask = new ActualizarDatosTask(this);
            DatosDTO datosActualizados = new DatosDTO();
            datosActualizados.setNombre(nombre.getText().toString());
            datosActualizados.setApellidos(apellidos.getText().toString());
            actualizarDatosTask.execute(datosActualizados);
            finish();
        });

    }
}