package com.example.app_actuni;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText etiuser, eticont;
    private Button button2, button;
    AdminSQLiteOpenHelper admin;

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
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        etiuser = findViewById(R.id.et1Is);
        eticont = findViewById(R.id.et2Cis);

        admin = new AdminSQLiteOpenHelper(this, "db1", null, 1);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistroCliente.class);
                startActivity(intent);
            }
        });

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

    }



}