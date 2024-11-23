package com.example.app_actuni;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {
    private Cursor cursor;
    SQLiteDatabase db;
    AdminSQLiteOpenHelper admin;
    private Carrito carrito;

    public CarritoAdapter(Cursor cursor, SQLiteDatabase db, Carrito carrito) {
        this.db = db;
        this.cursor = cursor;
        this.carrito = carrito;
    }

    @NonNull
    @Override
    public CarritoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        cursor.moveToPosition(position);

        int idProducto = cursor.getInt(cursor.getColumnIndexOrThrow("producto_id"));
        int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));

        Cursor producCursor = db.rawQuery("SELECT * FROM Productos WHERE id = ?", new String[]{String.valueOf(idProducto)});
        producCursor.moveToFirst();
        String nombre = producCursor.getString(producCursor.getColumnIndexOrThrow("nombre"));
        double precio = producCursor.getDouble(producCursor.getColumnIndexOrThrow("precio"));

        holder.tvNombre.setText(nombre);
        holder.tvCantidad.setText("Cantidad: " + cantidad);
        holder.tvPrecio.setText("$" + precio);

        holder.beliminar.setOnClickListener(v -> {
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            int idProducto1 = cursor.getInt(cursor.getColumnIndexOrThrow("producto_id"));

            eliminarProductoCarrito(idProducto1, position);

            carrito.cargarDatosCarrito();
        });

        producCursor.close();

    }

    public Cursor getCursor() {
        return cursor;
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button beliminar;
        public TextView tvNombre;
        public TextView tvPrecio;
        public TextView tvCantidad;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvnombrec);
            tvCantidad = itemView.findViewById(R.id.tvcantc);
            tvPrecio = itemView.findViewById(R.id.tvprecioc);
            beliminar = itemView.findViewById(R.id.btnEliminar);

        }
    }

    private void eliminarProductoCarrito(int idProducto, int position) {
        db.delete("Carrito", "producto_id = ?", new String[]{String.valueOf(idProducto)});
        carrito.cargarDatosCarrito();
        notifyItemRemoved(position);
    }

}
