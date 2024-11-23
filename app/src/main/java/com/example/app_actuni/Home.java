package com.example.app_actuni;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Home extends AppCompatActivity {

    private RecyclerView rvProduct;
    private ProductoAdapter adapter;
    AdminSQLiteOpenHelper admin;
    private ImageButton ibCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ibCarrito = findViewById(R.id.ibCarrito);

        ibCarrito.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Carrito.class);
            startActivity(intent);
        });

        rvProduct = findViewById(R.id.rvProductos);

        admin = new AdminSQLiteOpenHelper(this, "db1", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Productos", null);

        adapter = new ProductoAdapter(this, cursor);
        rvProduct.setAdapter(adapter);
        rvProduct.setLayoutManager(new LinearLayoutManager(this));

    }

    public void mostrarPopup(View v) {
        ImageView ivMenu = findViewById(R.id.ibMenu);
        PopupMenu pm = new PopupMenu(this, ivMenu);

        pm.getMenuInflater().inflate(R.menu.menupopup, pm.getMenu());
        pm.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.iradmin) {
                    Toast.makeText(Home.this, "Administración", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Home.this, AdminActivity.class);
                    startActivity(intent);
                    return true;
            } else if (item.getItemId() == R.id.irLocalizacion) {
                Toast.makeText(Home.this, "Localización", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, GeoLocalizacion.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.cerrarSesion) {
                Toast.makeText(Home.this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        });

        pm.show();

    }

}
