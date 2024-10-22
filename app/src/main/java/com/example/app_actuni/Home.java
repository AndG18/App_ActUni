package com.example.app_actuni;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {
    private ImageButton ibCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        ibCarrito = findViewById(R.id.ibCarrito);

        ibCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Carrito.class);
                startActivity(intent);
            }
        });
    }

}
