package com.example.app_actuni;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {
    private AdminSQLiteOpenHelper admin;
    private Context context;
    private Cursor cursor;

    public ProductoAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.admin = new AdminSQLiteOpenHelper(context, "db1", null, 1);
    }

    @NonNull
    @Override
    public ProductoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        cursor.moveToPosition(position);

        holder.tvNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        holder.tvDescrip.setText(cursor.getString(cursor.getColumnIndexOrThrow("descripcion")));
        holder.tvMarca.setText("Marca: " + cursor.getString(cursor.getColumnIndexOrThrow("marca")));
        holder.tvColor.setText("Color: " + cursor.getString(cursor.getColumnIndexOrThrow("color")));
        holder.tvTamano.setText(cursor.getString(cursor.getColumnIndexOrThrow("tamaño")));
        holder.tvPrecio.setText("Precio: $" + cursor.getString(cursor.getColumnIndexOrThrow("precio")));
        holder.tvStock.setText("Stock: " + cursor.getString(cursor.getColumnIndexOrThrow("stock")));
        
        holder.agregarCarrito.setOnClickListener(v -> {
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            int idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("id"));

            agregarAlCarrito(idProducto);
        });

    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    private void agregarAlCarrito(int idProducto) {
        
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            ContentValues valores = new ContentValues();
            valores.put("producto_id", idProducto);
            valores.put("cantidad", 1);
            db.insert("Carrito", null, valores);
        } catch (Exception e) {
            Toast.makeText(context, "No se pudo agregar el producto al carrito: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombre, tvDescrip, tvMarca, tvColor, tvTamano, tvPrecio, tvStock;
        public Button agregarCarrito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreP);
            tvDescrip = itemView.findViewById(R.id.tvDescripP);
            tvMarca = itemView.findViewById(R.id.tvMarcaP);
            tvColor = itemView.findViewById(R.id.tvColorP);
            tvTamano = itemView.findViewById(R.id.tcTamanoP);
            tvPrecio = itemView.findViewById(R.id.tvPrecioP);
            tvStock = itemView.findViewById(R.id.tvStockP);
            agregarCarrito = itemView.findViewById(R.id.agregarCarrito);

        }
    }
}
