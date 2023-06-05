package es.pfc.dacloud;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class ConfiguracionActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.config);

        Preference modificarDatos  = findPreference("modificar_datos");
        modificarDatos .setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(ConfiguracionActivity.this, DatosPersonalesActivity.class);
            startActivity(intent);
            return true;
        });

        Preference seleccionarSuscripcion  = findPreference("seleccionar_suscripcion");
        seleccionarSuscripcion .setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(ConfiguracionActivity.this, CambiarSuscripcionActivity.class);
            startActivity(intent);
            return true;
        });
    }
}

