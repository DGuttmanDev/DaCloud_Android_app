package es.pfc.dacloud.business.service.session;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.File;
import es.pfc.dacloud.business.dto.LoginDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginService implements Runnable {
    private static final String LOGIN_URL = "HTTP://192.168.0.19:8080/api/session/login";

    private LoginDTO loginDTO;
    private Context context;
    private Activity activity;

    public LoginService(LoginDTO loginDTO, Context context, Activity activity) {
        this.loginDTO = loginDTO;
        this.context = context;
        this.activity = activity;
    }

    public void run() {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, gson.toJson(loginDTO));

        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Error en la petición: " + response);
            }

            // Obtener el token JWT de la respuesta
            String token = response.body().string();

            // Hacer algo con el token JWT, por ejemplo, guardarlo en SharedPreferences
            SharedPreferences preferences = context.getSharedPreferences("AuthPreferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("token", token);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
