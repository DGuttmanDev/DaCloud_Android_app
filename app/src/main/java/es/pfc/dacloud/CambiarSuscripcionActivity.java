package es.pfc.dacloud;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import es.pfc.dacloud.business.task.CambiarSuscripcionTask;
import es.pfc.dacloud.business.task.SuscripcionActualTask;

public class CambiarSuscripcionActivity extends AppCompatActivity {

    private Spinner opcionesSuscripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_suscripcion);

        opcionesSuscripcion = findViewById(R.id.opciones_suscripcion);

        SuscripcionActualTask suscripcionActualTask = new SuscripcionActualTask(this);
        try {
            String suscripcion = suscripcionActualTask.execute().get();
            Log.d("Suscripcion", suscripcion);
            SpinnerAdapter adapter = opcionesSuscripcion.getAdapter();

            int posicionOpcion = 0;
            for (int i = 0; i < adapter.getCount(); i++) {
                if (suscripcion.contains(adapter.getItem(i).toString())) {
                    posicionOpcion = i;
                    break;
                }
            }
            Log.d("Suscripcion index ", String.valueOf(posicionOpcion));
            opcionesSuscripcion.setSelection(posicionOpcion);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Button botonAplicar = findViewById(R.id.boton_aplicar);
        botonAplicar.setOnClickListener(v -> {
            String suscripcionSeleccionada = opcionesSuscripcion.getSelectedItem().toString();
            CambiarSuscripcionTask cambiarSuscripcionTask = new CambiarSuscripcionTask(this);
            cambiarSuscripcionTask.execute(suscripcionSeleccionada);
            finish();
        });
    }

}