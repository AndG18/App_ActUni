package com.example.app_actuni;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Carrito extends AppCompatActivity {
    private RecyclerView rvCarrito;
    private CarritoAdapter adapter;
    AdminSQLiteOpenHelper admin;
    SQLiteDatabase db;
    private TextView tvtotal;
    private ImageButton ibvolver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carrito);

        ibvolver = findViewById(R.id.ib_volver);

        ibvolver.setOnClickListener(v -> {
            Intent intent = new Intent(Carrito.this, Home.class);
            startActivity(intent);
        });

        admin = new AdminSQLiteOpenHelper(this, "db1", null, 1);
        db = admin.getWritableDatabase();

        rvCarrito = findViewById(R.id.rvCarritoc);
        tvtotal = findViewById(R.id.tvTotal);

        cargarDatosCarrito();

    }

    void cargarDatosCarrito() {
        Cursor cursor = db.rawQuery("SELECT * FROM  Carrito", null);

        adapter = new CarritoAdapter(cursor, db, this);

        rvCarrito.setAdapter(adapter);
        rvCarrito.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        double total = calcularTotalC();

        tvtotal.setText("Total: $" + total);

    }

    private double calcularTotalC() {
        double total = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM Carrito", null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return 0;
        }

        if (cursor.moveToFirst()) {
            do {
                int idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("producto_id"));
                int cant = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));

                Cursor prodCursor = db.rawQuery("SELECT * FROM Productos WHERE id = ?", new String[]{String.valueOf(idProducto)});

                if (prodCursor.moveToFirst()) {
                    double precio = prodCursor.getDouble(prodCursor.getColumnIndexOrThrow("precio"));
                    total += precio * cant;
                }
                prodCursor.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
        return total;
    }

}
