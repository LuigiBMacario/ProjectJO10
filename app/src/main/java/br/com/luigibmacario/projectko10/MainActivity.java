package br.com.luigibmacario.projectko10;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/LuigiBMacario/ProjectKO10/master/version.json";
    private static Context gameContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        gameContext = this;
        setContentView(new GamePanel(this));
        checkForUpdate(this);
    }
    public static Context getGameContext(){
        return gameContext;
    }
    private void checkForUpdate(Context context) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(UPDATE_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("UPDATE_CHECK", "Erro ao verificar atualização: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String jsonString = response.body().string();
                    try {
                        JSONObject json = new JSONObject(jsonString);
                        String latestVersionName = json.getString("versionName");
                        String downloadUrl = json.getString("downloadUrl");

                        // Obter a versão atual
                        PackageManager packageManager = context.getPackageManager();
                        PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                        String currentVersionName = packageInfo.versionName;

                        // Comparar versões
                        if (isNewerVersion(currentVersionName, latestVersionName)) {
                            runOnUiThread(() -> new AlertDialog.Builder(context)
                                    .setTitle("Atualização disponível")
                                    .setMessage("Uma nova versão do app está disponível. Deseja atualizar?")
                                    .setPositiveButton("Atualizar", (dialog, which) -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                                        context.startActivity(intent);
                                    })
                                    .setNegativeButton("Agora não", null)
                                    .show());
                        }

                    } catch (Exception e) {
                        Log.e("UPDATE_CHECK", "Erro ao analisar JSON: " + e.getMessage());
                    }
                }
            }
        });
    }

    private boolean isNewerVersion(String currentVersionName, String latestVersionName) {
        // Comparação simples de versões, você pode melhorar isso se precisar de lógica mais complexa
        return latestVersionName.compareTo(currentVersionName) > 0;
    }

}

