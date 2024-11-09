package com.example.app_actuni;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroCliente extends AppCompatActivity {

    EditText etnombre, etapellido, etcorreo, ettelefono, etuser, etcontra;
    AdminSQLiteOpenHelper admin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_cliente);

        etnombre = findViewById(R.id.ett1rnombre);
        etapellido = findViewById(R.id.ett2rapellido);
        etcorreo = findViewById(R.id.ett3rcorreo);
        ettelefono = findViewById(R.id.ett4rtel);
        etuser = findViewById(R.id.ett5ruser);
        etcontra = findViewById(R.id.ett6rcont);

        admin = new AdminSQLiteOpenHelper(this, "db1", null, 1);

    }

    public void registrarUser(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();

        if (etnombre.getText().toString().isEmpty() || etapellido.getText().toString().isEmpty() ||
                etcorreo.getText().toString().isEmpty() || ettelefono.getText().toString().isEmpty() ||
                etuser.getText().toString().isEmpty() || etcontra.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!correoValido(etcorreo.getText().toString())){
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        registro.put("nombre", etnombre.getText().toString());
        registro.put("apellido", etapellido.getText().toString());
        registro.put("correo", etcorreo.getText().toString());
        registro.put("telefono", ettelefono.getText().toString());
        registro.put("nombre_usuario", etuser.getText().toString());
        registro.put("contraseña", etcontra.getText().toString());

        try {
            db.insert("Usuarios", null, registro);
            Toast.makeText(this, "Se ha registrado el usuario", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }

        etnombre.setText("");
        etapellido.setText("");
        etcorreo.setText("");
        ettelefono.setText("");
        etuser.setText("");
        etcontra.setText("");

    }

    //Validar correo
    public boolean correoValido(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
