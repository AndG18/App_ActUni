package com.example.app_actuni;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivity extends AppCompatActivity {

    EditText etunombre, etuapellido, etucorreo, etutelefono, etuuser, etucontra;
    EditText etcatid, etcatnombre;
    EditText ettipid, ettipnombre;
    EditText etpid, etpnombre, etpdescrip, etpmarca, etpcolor, etptamano, etpprecio, etpstock;
    AdminSQLiteOpenHelper admin;

    private Spinner spTble;
    private String[] tablas = {" " ,"Usuarios", "Categorias", "Tipos", "Productos"};

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etunombre = findViewById(R.id.etnombread);
        etuapellido = findViewById(R.id.etapellidoad);
        etucorreo = findViewById(R.id.etcorreoad);
        etutelefono = findViewById(R.id.ettelefonoad);
        etuuser = findViewById(R.id.etuserad);
        etucontra = findViewById(R.id.etcontraad);

        ettipid = findViewById(R.id.etid_cat_ad);
        etcatnombre = findViewById(R.id.etnom_cat_ad);

        ettipid = findViewById(R.id.etid_tip_ad);
        ettipnombre = findViewById(R.id.etnombre_tipo_ad);

        etpid = findViewById(R.id.etid_product_ad);
        etpnombre = findViewById(R.id.etnombre_product_ad);
        etpdescrip = findViewById(R.id.etdescrip_prod_ad);
        etpmarca = findViewById(R.id.etmarca_prod_ad);
        etpcolor = findViewById(R.id.etcolor_prod_ad);
        etptamano = findViewById(R.id.ettamano_prod_ad);
        etpprecio = findViewById(R.id.etprecio_prod_ad);
        etpstock = findViewById(R.id.etstock_prod_ad);

        admin = new AdminSQLiteOpenHelper(this, "db1", null, 1);

        spTble = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tablas);
        spTble.setAdapter(adapter);
        
        spTble.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tablaSelec = tablas[position];
                mostrarFormulario(tablaSelec);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void mostrarFormulario(String tablaSelec) {
        findViewById(R.id.formularioUsuarios).setVisibility(View.GONE);
        findViewById(R.id.formularioCategorias).setVisibility(View.GONE);
        findViewById(R.id.formularioTipos).setVisibility(View.GONE);
        findViewById(R.id.formularioProductos).setVisibility(View.GONE);
        findViewById(R.id.init).setVisibility(View.GONE);

        if (tablaSelec.equals(" ")){
            findViewById(R.id.init).setVisibility(View.VISIBLE);
        } else if (tablaSelec.equals("Usuarios")){
            findViewById(R.id.formularioUsuarios).setVisibility(View.VISIBLE);
        } else if (tablaSelec.equals("Categorias")) {
            findViewById(R.id.formularioCategorias).setVisibility(View.VISIBLE);
        } else if (tablaSelec.equals("Tipos")) {
            findViewById(R.id.formularioTipos).setVisibility(View.VISIBLE);
        } else if (tablaSelec.equals("Productos")) {
            findViewById(R.id.formularioProductos).setVisibility(View.VISIBLE);
        }
    }

    public void cerrar(View v) {
        Intent intent = new Intent(AdminActivity.this, Home.class);
        startActivity(intent);
    }

    //CRUD Tabla Usuarios
    public void crearUser(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();

        if (etunombre.getText().toString().isEmpty() || etuapellido.getText().toString().isEmpty() ||
                etucorreo.getText().toString().isEmpty() || etutelefono.getText().toString().isEmpty() ||
                etuuser.getText().toString().isEmpty() || etucontra.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!correoValido(etucorreo.getText().toString())){
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        registro.put("nombre", etunombre.getText().toString());
        registro.put("apellido", etuapellido.getText().toString());
        registro.put("correo", etucorreo.getText().toString());
        registro.put("telefono", etutelefono.getText().toString());
        registro.put("nombre_usuario", etuuser.getText().toString());
        registro.put("contraseña", etucontra.getText().toString());

        try {
            db.insert("Usuarios", null, registro);
            Toast.makeText(this, "Se ha registrado el usuario", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }

        etunombre.setText("");
        etuapellido.setText("");
        etucorreo.setText("");
        etutelefono.setText("");
        etuuser.setText("");
        etucontra.setText("");

    }

    //Validar correo
    public boolean correoValido(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void consultarUser(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String user = etuuser.getText().toString();

        Cursor fila = db.rawQuery("SELECT nombre, apellido, correo, telefono, contraseña " +
                "FROM Usuarios WHERE nombre_usuario = '" + user + "'", null);

        if (fila.moveToFirst()) {
            etunombre.setText(fila.getString(fila.getColumnIndexOrThrow("nombre")));
            etuapellido.setText(fila.getString(fila.getColumnIndexOrThrow("apellido")));
            etucorreo.setText(fila.getString(fila.getColumnIndexOrThrow("correo")));
            etutelefono.setText(fila.getString(fila.getColumnIndexOrThrow("telefono")));
            etucontra.setText(fila.getString(fila.getColumnIndexOrThrow("contraseña")));

        } else {
            Toast.makeText(this, "No se ha encontrado el usuario", Toast.LENGTH_SHORT).show();
            etunombre.setText("");
            etuapellido.setText("");
            etucorreo.setText("");
            etutelefono.setText("");
            etuuser.setText("");
            etucontra.setText("");
        }
        db.close();
    }

    public void modificarUser(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String user = etuuser.getText().toString();

        ContentValues registro = new ContentValues();

        registro.put("nombre", etunombre.getText().toString());
        registro.put("apellido", etuapellido.getText().toString());
        registro.put("correo", etucorreo.getText().toString());
        registro.put("telefono", etutelefono.getText().toString());
        registro.put("nombre_usuario", etuuser.getText().toString());
        registro.put("contraseña", etucontra.getText().toString());

        int cant = db.update("Usuarios", registro,"nombre_usuario = '" + user + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha modificado la información del usuario", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No se ha encontrado el usuario", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void eliminarUser(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String user = etuuser.getText().toString();

        int cant = db.delete("Usuarios", "nombre_usuario = '" + user + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha eliminado el usuario", Toast.LENGTH_SHORT).show();
            etunombre.setText("");
            etuapellido.setText("");
            etucorreo.setText("");
            etutelefono.setText("");
            etuuser.setText("");
            etucontra.setText("");

        } else {
            Toast.makeText(this, "No se ha encontrado el usuario", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    // CRUD Tabla Categorias
    public void crearCat(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registroC = new ContentValues();

        if (etcatnombre.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        registroC.put("nombre", etcatnombre.getText().toString());

        try {
            db.insert("Categorias", null, registroC);
            Toast.makeText(this, "Se ha creado una nueva categoria", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al crear una nueva categoria: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
        etcatnombre.setText("");
    }


    public void consultarCat(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String catid = etcatid.getText().toString();

        Cursor fila = db.rawQuery("SELECT * FROM Categorias WHERE id = '" + catid + "'", null);

        if (fila.moveToFirst()) {
            etcatnombre.setText(fila.getString(fila.getColumnIndexOrThrow("nombre")));
            etcatid.setText("");
        } else {
            Toast.makeText(this, "No se ha encontrado la categoria", Toast.LENGTH_SHORT).show();
            etcatnombre.setText("");
        }
        db.close();
    }

    public void modificarCat(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String cat_id = etcatid.getText().toString();

        ContentValues registroC = new ContentValues();

        registroC.put("nombre", etcatnombre.getText().toString());

        int cant = db.update("Categorias", registroC,"id = '" + cat_id + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha modificado la categoria", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No se ha encontrado la categoria", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void eliminarCat(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String idcat = etcatid.getText().toString();

        int cant = db.delete("Categorias", "id = '" + idcat + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha eliminado la categoria", Toast.LENGTH_SHORT).show();
            etcatid.setText("");
            etcatnombre.setText("");

        } else {
            Toast.makeText(this, "No se ha encontrado la categoria", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    //CRUD Tabla Tipos
    public void crearT(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registroT = new ContentValues();

        if (ettipnombre.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }


        registroT.put("nombre", ettipnombre.getText().toString());

        try {
            db.insert("Tipos", null, registroT);
            Toast.makeText(this, "Se ha creado un nuevo tipo", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al crear un nuevo tipo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }
        ettipnombre.setText("");
    }


    public void consultT(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String tipid = ettipid.getText().toString();

        Cursor fila = db.rawQuery("SELECT * FROM Tipo WHERE id = '" + tipid + "'", null);

        if (fila.moveToFirst()) {
            ettipnombre.setText(fila.getString(fila.getColumnIndexOrThrow("nombre")));

        } else {
            Toast.makeText(this, "No se ha encontrado el tipo", Toast.LENGTH_SHORT).show();
            ettipnombre.setText("");
        }
        db.close();
    }

    public void modT(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String idtip = ettipid.getText().toString();

        ContentValues registroT = new ContentValues();

        registroT.put("nombre", ettipnombre.getText().toString());

        int cant = db.update("Tipos", registroT,"id = '" + idtip + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha modificado el tipo", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No se ha encontrado el tipo", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void eliminarT(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String idt = ettipid.getText().toString();

        int cant = db.delete("Tipos", "nombre = '" + idt + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha eliminado el tipo", Toast.LENGTH_SHORT).show();
            ettipnombre.setText("");
            ettipid.setText("");

        } else {
            Toast.makeText(this, "No se ha encontrado el tipo", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    //CRUD Tabla Productos
    public void crearP(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues registroP = new ContentValues();

        if (etpnombre.getText().toString().isEmpty() || etpdescrip.getText().toString().isEmpty() ||
                etpmarca.getText().toString().isEmpty() || etpcolor.getText().toString().isEmpty() ||
                etptamano.getText().toString().isEmpty() || etpprecio.getText().toString().isEmpty() ||
                etpstock.getText().toString().isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        registroP.put("nombre", etpnombre.getText().toString());
        registroP.put("descripcion", etpdescrip.getText().toString());
        registroP.put("marca", etpmarca.getText().toString());
        registroP.put("color", etpcolor.getText().toString());
        registroP.put("tamaño", etptamano.getText().toString());
        registroP.put("precio", etpprecio.getText().toString());
        registroP.put("stock", etpstock.getText().toString());

        try {
            db.insert("Productos", null, registroP);
            Toast.makeText(this, "Se ha registrado el producto", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al registrar el producto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            db.close();
        }

        etpnombre.setText("");
        etpdescrip.setText("");
        etpmarca.setText("");
        etpcolor.setText("");
        etptamano.setText("");
        etpprecio.setText("");
        etpstock.setText("");

    }

    public void consultP(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String id = etpid.getText().toString();

        Cursor fila = db.rawQuery("SELECT * FROM Productos WHERE id = '" + id + "'", null);

        if (fila.moveToFirst()) {
            etpnombre.setText(fila.getString(fila.getColumnIndexOrThrow("nombre")));
            etpdescrip.setText(fila.getString(fila.getColumnIndexOrThrow("descripcion")));
            etpmarca.setText(fila.getString(fila.getColumnIndexOrThrow("marca")));
            etpcolor.setText(fila.getString(fila.getColumnIndexOrThrow("color")));
            etptamano.setText(fila.getString(fila.getColumnIndexOrThrow("tamaño")));
            etpprecio.setText(fila.getString(fila.getColumnIndexOrThrow("precio")));
            etpstock.setText(fila.getString(fila.getColumnIndexOrThrow("stock")));

        } else {
            Toast.makeText(this, "No se ha encontrado el producto", Toast.LENGTH_SHORT).show();
            etpid.setText("");
            etpnombre.setText("");
            etpdescrip.setText("");
            etpmarca.setText("");
            etpcolor.setText("");
            etptamano.setText("");
            etpprecio.setText("");
            etpstock.setText("");
        }
        db.close();
    }

    public void modP(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String id = etpid.getText().toString();

        ContentValues registroP = new ContentValues();

        registroP.put("nombre", etpnombre.getText().toString());
        registroP.put("descripcion", etpdescrip.getText().toString());
        registroP.put("marca", etpmarca.getText().toString());
        registroP.put("color", etpcolor.getText().toString());
        registroP.put("tamaño", etptamano.getText().toString());
        registroP.put("precio", etpprecio.getText().toString());
        registroP.put("stock", etpstock.getText().toString());

        int cant = db.update("Productos", registroP,"id = '" + id + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha modificado la información del producto", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No se ha encontrado el producto", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public void eliminarP(View v) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String id = etpid.getText().toString();

        int cant = db.delete("Productos", "id = '" + id + "'", null);

        if (cant == 1) {
            Toast.makeText(this, "Se ha eliminado el producto", Toast.LENGTH_SHORT).show();
            etpid.setText("");
            etpnombre.setText("");
            etpdescrip.setText("");
            etpmarca.setText("");
            etpcolor.setText("");
            etptamano.setText("");
            etpprecio.setText("");
            etpstock.setText("");

        } else {
            Toast.makeText(this, "No se ha encontrado el producto", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}