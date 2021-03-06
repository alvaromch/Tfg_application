package com.example.tfg_plication;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tfg_plication.db.ControllerDB;
import com.example.tfg_plication.db.ControllerFB;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button buttonRecipe;
    ControllerFB controllerFB;
    private Button buttonBreakfast;
    private Button buttonMainCourse;
    private Button buttonDessert;
    private Button buttonAddRecipe;
    private final String FONT_TITTLE = "StreetExplorer.otf";
    private Typeface font;
    private TextView app_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonBreakfast = findViewById(R.id.buttonStarters);
        buttonMainCourse = findViewById(R.id.buttonMainCourses);
        buttonDessert = findViewById(R.id.buttonDesserts);
        buttonRecipe = findViewById(R.id.allRecipes);
        //controllerFB = new ControllerFB(this);
        buttonAddRecipe = findViewById(R.id.addRecipe);

        //updateFont();

        int idUser = this.getIntent().getExtras().getInt("idUser");

        buttonRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID_USER  = getIntent().getExtras().getInt("idUser");
                Intent intentListRecipes = new Intent(MainActivity.this, ListRecipes.class);
                if (ID_USER == 0){
                    ID_USER = getIntent().getExtras().getInt("returnIdToMain");
                }
                intentListRecipes.putExtra("idUser", ID_USER);
                startActivity(intentListRecipes);
            }
        });

        buttonAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> listIng = (ArrayList<String>) getIntent().getStringArrayListExtra("defaultIngredients");
                Intent intentListRecipes = new Intent(MainActivity.this, AddRecipe.class);
                if (listIng != null) {
                    intentListRecipes.putExtra("defaultIngredients", listIng);
                }
                int ID_USER = getIntent().getExtras().getInt("idUser");
                if (ID_USER != 0) {
                    intentListRecipes.putExtra("idUser", ID_USER);
                    Toast.makeText(MainActivity.this, "Main Activity-->" + ID_USER, Toast.LENGTH_SHORT).show();
                } else {
                    int id = getIntent().getExtras().getInt("returnIdToMain");
                    intentListRecipes.putExtra("idUser", id);
                    Toast.makeText(MainActivity.this, "Main Activity-->" + id, Toast.LENGTH_SHORT).show();
                }
                startActivity(intentListRecipes);
            }
        });

        buttonBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID_USER  = getIntent().getExtras().getInt("idUser");
                Intent intentBreakfast = new Intent (MainActivity.this, ShowRecipe.class);
                if (ID_USER == 0){
                    ID_USER = getIntent().getExtras().getInt("returnIdToMain");
                }
                intentBreakfast.putExtra("idUser", ID_USER);
                intentBreakfast.putExtra("bf","Desayuno");
                startActivity(intentBreakfast);
            }
        });
        buttonMainCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID_USER  = getIntent().getExtras().getInt("idUser");
                Intent intentBreakfast = new Intent (MainActivity.this, ShowRecipe.class);
                if (ID_USER == 0){
                    ID_USER = getIntent().getExtras().getInt("returnIdToMain");
                }
                intentBreakfast.putExtra("idUser", ID_USER);
                intentBreakfast.putExtra("eat","Comida");
                startActivity(intentBreakfast);
            }
        });
        buttonDessert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int ID_USER  = getIntent().getExtras().getInt("idUser");
                Intent dinner = new Intent (MainActivity.this, ShowRecipe.class);
                if (ID_USER == 0){
                    ID_USER = getIntent().getExtras().getInt("returnIdToMain");
                }
                dinner.putExtra("idUser", ID_USER);
                dinner.putExtra("din","Cena");
                startActivity(dinner);
            }
        });

    }

    private void updateFont() {
        font = Typeface.createFromAsset(getAssets(), FONT_TITTLE);
        app_name = (TextView) findViewById(R.id.tws);
        app_name.setTypeface(font);
    }
}