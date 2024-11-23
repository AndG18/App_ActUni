package com.example.app_actuni;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private EditText etiuser, eticont;
    private Button button2, btn_google;
    AdminSQLiteOpenHelper admin;
    ImageView ivMostrarCont;

    private static final String TAG = "InicioDeSesion";
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;
    private boolean showOneTapUI = true;

    private int intentosFailedLogin = 0;
    private static final int MAX_INTENTOS = 5;
    private static final long TIEMPO_BLOQUEO = 30 * 60 * 1000; // 30 minutos en milisegundos

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivMostrarCont = findViewById(R.id.iv_show_contra);
        button2 = findViewById(R.id.button2);
        etiuser = findViewById(R.id.et1Is);
        eticont = findViewById(R.id.et2Cis);

        admin = new AdminSQLiteOpenHelper(this, "db1", null, 1);

        button2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroCliente.class);
            startActivity(intent);
        });

        ivMostrarCont.setOnClickListener(v -> {
            if (eticont.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                eticont.setTransformationMethod(null);
                ivMostrarCont.setImageResource(R.drawable.icono_ojocerrado);
            } else {
                eticont.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ivMostrarCont.setImageResource(R.drawable.icono_ojoabierto);
            }
        });

        inicializarVistas();
        configurarGoogleSignIn();
        configurarActivityResultLauncher();
        configurarBotonLogin();

    }

    private void inicializarVistas() {
        btn_google = findViewById(R.id.btn_google);
        Log.d(TAG, "Vistas iniciadas");
    }

    public void inicioSes(View v) {

        String user = etiuser.getText().toString();
        String contra = eticont.getText().toString();

        if (user.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = admin.getWritableDatabase();
        String sql = "Select * FROM Usuarios WHERE nombre_usuario = '" + user + "' AND contraseña = '" + contra + "'";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Bienvenido,  " + user, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, Home.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();

        etiuser.setText("");
        eticont.setText("");
    }

    private void configurarGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.web_client))
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .setAutoSelectEnabled(true)
                .build();
        Log.d(TAG, "Google Sign-In configurado");
    }

    private void configurarActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                String email = credential.getId();
                                Log.d(TAG, "Google Sign-In exitoso para: " + email);
                                Toast.makeText(getApplicationContext(), "Email: " + email, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                startActivity(intent);
                            } else {
                                Log.w(TAG, "No se pudo obtener ID Token");
                            }
                        } catch (ApiException e) {
                            Log.e(TAG, "Error al obtener credenciales: " + e.getStatusCode(), e);
                            manejarErrorLogin(e);
                        }
                    } else {
                        Log.w(TAG, "Google Sign-In cancelado");
                    }
                    btn_google.setEnabled(true);
                });
        Log.d(TAG, "ActivityResultLauncher configurado");
    }

    private void configurarBotonLogin() {
        btn_google.setOnClickListener(v -> {
            Log.d(TAG, "Botón de inicio de sesión presionado");
            if (intentosFailedLogin >= MAX_INTENTOS) {
                Toast.makeText(MainActivity.this, "Has alcanzado el límite de intentos. Por favor, espera antes de intentar de nuevo.", Toast.LENGTH_LONG).show();
                return;
            }
            iniciarProcesoLogin();
        });
    }

    private void iniciarProcesoLogin() {
        Log.d(TAG, "Iniciando proceso de login");
        btn_google.setEnabled(false);

        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(MainActivity.this, result -> {
                    Log.d(TAG, "beginSignIn exitoso");
                    IntentSenderRequest intentSenderRequest =
                            new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                    activityResultLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener(MainActivity.this, e -> {
                    Log.e(TAG, "beginSignIn falló", e);
                    btn_google.setEnabled(true);
                    manejarErrorLogin(e);
                });
    }

    private void manejarErrorLogin(Exception e) {
        intentosFailedLogin++;
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            if (apiException.getStatusCode() == CommonStatusCodes.CANCELED) {
                Log.d(TAG, "One-tap dialog was closed.");
                // Don't re-prompt immediately after the user cancels.
                showOneTapUI = false;
            } else {
                Log.e(TAG, "Error de inicio de sesión: " + apiException.getStatusCode());
                Toast.makeText(MainActivity.this, "Error al iniciar sesión: " + getErrorMessage(apiException.getStatusCode()), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e(TAG, "Error no específico: " + e.getMessage(), e);
            Toast.makeText(MainActivity.this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (intentosFailedLogin >= MAX_INTENTOS) {
            Log.w(TAG, "Máximo de intentos alcanzado");
            Toast.makeText(MainActivity.this, "Has alcanzado el límite de intentos fallidos. Inténtalo de nuevo más tarde.", Toast.LENGTH_LONG).show();
            btn_google.setEnabled(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    btn_google.setEnabled(true);
                    intentosFailedLogin = 0;
                    Log.d(TAG, "Reinicio de intentos de login");
                }
            }, TIEMPO_BLOQUEO);
        }
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            Log.e(TAG, "Código de error detallado: " + apiException.getStatusCode());
            Log.e(TAG, "Mensaje de error detallado: " + apiException.getMessage());
        }
    }
    private String getErrorMessage(int statusCode) {
        switch (statusCode) {
            case CommonStatusCodes.API_NOT_CONNECTED:
                return "API no conectada";
            case CommonStatusCodes.DEVELOPER_ERROR:
                return "Error de desarrollador. Verifica la configuración.";
            case CommonStatusCodes.ERROR:
                return "Error desconocido";
            case CommonStatusCodes.INTERNAL_ERROR:
                return "Error interno de Google Play Services";
            case CommonStatusCodes.INVALID_ACCOUNT:
                return "Cuenta inválida";
            case CommonStatusCodes.SIGN_IN_REQUIRED:
                return "Se requiere iniciar sesión";
            default:
                return "Error " + statusCode;
        }
    }

}