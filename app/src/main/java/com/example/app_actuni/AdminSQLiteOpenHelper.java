package com.example.app_actuni;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Usuarios (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  nombre TEXT,\n" +
                "  apellido TEXT,\n" +
                "  correo TEXT,\n" +
                "  telefono TEXT,\n" +
                "  nombre_usuario TEXT,\n" +
                "  contraseña TEXT\n" +
                ");");

        db.execSQL("CREATE TABLE Categorias (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  nombre TEXT\n" +
                ");");

        db.execSQL("CREATE TABLE Tipos (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  nombre TEXT\n" +
                ");");

        db.execSQL("CREATE TABLE Productos (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  nombre TEXT,\n" +
                "  descripcion TEXT,\n" +
                "  marca TEXT,\n" +
                "  color TEXT,\n" +
                "  tamaño TEXT,\n" +
                "  precio REAL,\n" +
                "  stock INTEGER,\n" +
                "  categoria_id INTEGER  REFERENCES Categorias,\n" +
                "  tipo_id INTEGER REFERENCES Tipos(id)\n" +
                ");");

        db.execSQL("CREATE TABLE Carrito (\n" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "  usuario_id INTEGER REFERENCES Usuarios(id),\n" +
                "  producto_id  INTEGER REFERENCES Productos(id),\n" +
                "  cantidad INTEGER \n" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
