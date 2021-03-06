package com.example.tfg_plication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.tfg_plication.entity.Ingredient;
import com.example.tfg_plication.entity.Recipe;
import com.example.tfg_plication.entity.RecipeIngredient;
import com.example.tfg_plication.entity.User;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ControllerDB extends SQLiteOpenHelper {

    public ControllerDB(Context context) {
        super(context, "com.damedix.Tfg_application", null, 16);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USERS (ID INTEGER PRIMARY KEY AUTOINCREMENT,USER TEXT NOT NULL,PASS TEXT NOT NULL);");
        db.execSQL("CREATE TABLE INGREDIENTS (ID INTEGER PRIMARY KEY AUTOINCREMENT,INGREDIENT TEXT NOT NULL);");
        db.execSQL("CREATE TABLE RECIPES (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INT NOT NULL," +
                "NAME_RECIPE TEXT NOT NULL," +
                "RECIPE_TEXT TEXT NOT NULL ," +
                "FATTEN TEXT ," +
                "TYPEOFFOOD TEXT," +
                "IMAGE BLOB," +
                "RATING FLOAT,"+
                "FOREIGN KEY (userId) REFERENCES USERS(ID));");
        db.execSQL("CREATE TABLE RECIPES_INGREDIENTS (ID_RECIPE INTEGER NOT NULL," +
                "ID_INGREDIENT INTEGER NOT NULL," +
                "AMOUNT INT NOT NULL," +
                "FOREIGN KEY (ID_RECIPE) REFERENCES RECIPES(ID)," +
                "FOREIGN KEY (ID_INGREDIENT) REFERENCES INGREDIENTS(ID));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //db.execSQL("ALTER TABLE RECIPES ADD COLUMN  RATING FLOAT");
    }

    public int checkIfUserExists(User user) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c1 = ref_db.query("USERS", new String[]{"ID"}, "USER=?", new String[]{user.getName()}, null, null, null);
        return c1.getCount();
    }

    public int checkIfPassExists(User user) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c1 = ref_db.rawQuery("SELECT ID FROM USERS WHERE USER=? AND PASS=?", new String[]{user.getName(), user.getPass()});
        return c1.getCount();
    }


    public User getUser(long id) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c1 = ref_db.rawQuery("SELECT ID FROM USERS WHERE USER=? ", new String[]{String.valueOf(id)});
        User user = new User();
        user.setId(id);
        user.setName(c1.getString(1));
        user.setPass(c1.getString(2));
        return user;
    }

    public void insertNewUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put("USER", user.getName());
        cv.put("PASS", user.getPass());
        SQLiteDatabase ref_db = this.getWritableDatabase();
        ref_db.insert("USERS", null, cv);
        ref_db.close();
    }

    /*public int getCantRegisters(User user) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c1 = ref_db.rawQuery("SELECT ID FROM USERS WHERE USER=?", new String[]{user.getName()});
        c1.moveToFirst();
        int id = c1.getInt(0);
        Cursor c2 = ref_db.rawQuery("SELECT TASK FROM TASKS WHERE userID=?", new String[]{String.valueOf(id)});
        return c2.getCount();
    }*/
    public void addIngredient(Ingredient ingredient) {
        SQLiteDatabase ref_db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("INGREDIENT", ingredient.getName());
        ref_db.insert("INGREDIENTS", null, cv);

        ref_db.close();
    }


    public List<Ingredient> getAllIngredient() {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM INGREDIENTS", null);
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(c2.getInt(0));
                ingredient.setName(c2.getString(1));
                ingredients.add(ingredient);
                c2.moveToNext();
            }
            ref_db.close();
            return ingredients;
        }
    }

    public void testAllIds() {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM INGREDIENTS", null);
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            //return null;
        } else {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(c2.getInt(0));
                Log.v("ControllerDB", "All ids -->" + c2.getInt(0));
                ingredient.setName(c2.getString(1));
                ingredients.add(ingredient);
                c2.moveToNext();
            }
            ref_db.close();
            //return ingredients;
        }

    }

    public void addRecipe(Recipe recipe) {
        SQLiteDatabase ref_db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("userId", recipe.getUser().getId());
        cv.put("NAME_RECIPE", String.valueOf(recipe.getName()));
        cv.put("RECIPE_TEXT", String.valueOf(recipe.getRecipeText()));
        cv.put("FATTEN", String.valueOf(recipe.getFatten()));
        cv.put("TYPEOFFOOD", String.valueOf(recipe.getTypeofFood()));
        cv.put("IMAGE", getBitmapAsByteArray(recipe.getImg()));
        cv.put("RATING", recipe.getRating());
        ref_db.insert("RECIPES", null, cv);
        Cursor c2 = ref_db.rawQuery("SELECT ID FROM RECIPES WHERE NAME_RECIPE=?", new String[]{recipe.getName()});
        c2.moveToFirst();
        int id = c2.getInt(0);

        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            ContentValues cv2 = new ContentValues();
            cv2.put("ID_RECIPE", id);
            cv2.put("ID_INGREDIENT", recipe.getIngredients().get(i).getIngredient().getId());
            cv2.put("AMOUNT", recipe.getIngredients().get(i).getAmount());
            ref_db.insert("RECIPES_INGREDIENTS", null, cv2);
            c2.moveToNext();
        }
        ref_db.close();
    }

    public void deleteRecipe(Recipe recipe) {
        SQLiteDatabase ref_db = this.getWritableDatabase();

        ref_db.delete("RECIPES", "RECIPE=? AND userID=?", new String[]{recipe.getName()});
        ref_db.close();
    }

    public List<Recipe> getRecipes(User user) {
        List<Recipe> recipes = new ArrayList<Recipe>();
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES WHERE userId = ?", new String[]{String.valueOf(user.getId())});
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c2.moveToFirst();

            String[] tasks = new String[cant_reg];
            for (int i = 0; i < cant_reg; i++) {
                Recipe recipe = new Recipe();
                recipe.setId(c2.getInt(0));
                recipe.setUser(user);
                recipe.setName(c2.getString(2));
                recipe.setRecipeText(c2.getString(3));
                recipe.setFatten(c2.getString(4));
                recipe.setTypeofFood(c2.getString(5));
                recipe.setImg(blobToBitmap(c2.getBlob(6)));
                recipe.setRating(c2.getFloat(7));
                recipes.add(recipe);
                c2.moveToNext();
            }
            ref_db.close();
            return recipes;
        }
    }

    public Ingredient getIngredient(SQLiteDatabase ref_db, int id) {
        ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM INGREDIENTS WHERE ID = ?", new String[]{String.valueOf(id)});
        c2.moveToFirst();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(c2.getInt(0));
        ingredient.setName(c2.getString(1));
        return ingredient;
    }

    public Recipe getRecipeIngredient(Recipe recipe) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Log.v("ControllerDB", "id-->" + recipe.getId());
        Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES_INGREDIENTS WHERE ID_RECIPE = ? ", new String[]{String.valueOf(recipe.getId())});
        c2.moveToFirst();
        ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<>();
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
        } else {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                RecipeIngredient recipeIngredient = new RecipeIngredient();

                Ingredient ingredient = getIngredient(ref_db, c2.getInt(1));
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setAmount(c2.getInt(2));

                recipeIngredients.add(recipeIngredient);

                //recipeIngredient.setRecipe(recipe);
                // recipeIngredient.setIngredient();
                //Cursor c3 = ref_db.rawQuery("SELECT * FROM INGREDIENTS WHERE ID = ?", new String[]{String.valueOf(c2.getInt(1))});
                //c3.moveToFirst();
                //Log.v("ControllerDB","Name Ingredient-->"+c3.getString(1));
                //Log.v("ControllerDB",""+c2.getInt(2));
                //recipeIngredient.setAmount(c2.getInt(2));
                //recipe.addIngredient(recipeIngredient);
                c2.moveToNext();
            }
            recipe.addListIngredient(recipeIngredients);
            ref_db.close();
            return recipe;
            //int id = c2.getInt(0);
        }
        return null;
    }

    /*public ShowRecipe getRecipeIngredient(ShowRecipe recipe) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES_INGREDIENTS WHERE ID_INGREDIENT = ? ", new String[]{String.valueOf(recipe.getId())});
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c2.moveToFirst();
            String[] tasks = new String[cant_reg];
            for (int i = 0; i < cant_reg; i++) {
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(recipe);
                recipeIngredient.setIngredient(getIngredient(c2.getInt(1)));
                recipeIngredient.setAmount(c2.getInt(2));
                recipe.addIngredient(recipeIngredient);
                c2.moveToNext();
            }
            ref_db.close();
            return recipe;
        }
    }*/


    public Bitmap blobToBitmap(byte[] img) {
        return BitmapFactory.decodeByteArray(img, 0, img.length);
    }

    public byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<Recipe>();
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES", null);
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                Recipe recipe = new Recipe();
                recipe.setId(c2.getInt(0));
                //recipe.setUser(getUser(c2.getInt(1)));
                recipe.setName(c2.getString(2));
                recipe.setRecipeText(c2.getString(3));
                recipe.setFatten(c2.getString(4));
                recipe.setTypeofFood(c2.getString(5));
                recipe.setImg(blobToBitmap(c2.getBlob(6)));
                //recipe.setRating(c2.getFloat(7));
                //Log.v("ControllerDB", "All_images-->" + blobToBitmap(c2.getBlob(6)));
                recipes.add(recipe);
                c2.moveToNext();
            }
            ref_db.close();
            return recipes;
        }
    }

    public void getTestRecipe() {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES", null);
        int cant_reg = c2.getCount();
        if (cant_reg != 0) {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                Log.v("ControllerDB", "id-->" + c2.getInt(0));
                Log.v("ControllerDB", "UserId" + c2.getInt(1));
                Log.v("ControllerDB", c2.getString(2));
                Log.v("ControllerDB", c2.getString(3));
                Log.v("ControllerDB", c2.getString(4));
                Log.v("ControllerDB", c2.getString(5));
                Log.v("ControllerDB", "Img-->" + blobToBitmap(c2.getBlob(6)));
                //Log.v("ControllerDB", String.valueOf(c2.getLong(7)));
                c2.moveToNext();
            }
        }
        ref_db.close();
    }

    public void getMoreInfo() {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES_INGREDIENTS", null);
        int cant_reg = c2.getCount();
        if (cant_reg != 0) {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                Log.v("ControllerDB", "ID_RECIPE -->" + c2.getInt(0));
                Log.v("ControllerDB", "ID_INGREDIENT -->" + c2.getInt(1));
                Log.v("ControllerDB", "ID_AMOUNT -->" + c2.getInt(2));
                c2.moveToNext();
            }
        }
    }

    /*public ShowRecipe getRecipe(int id)
    {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES WHERE id =1", new String[]{String.valueOf(id)});
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c2.moveToFirst();
            String[] tasks = new String[cant_reg];
                ShowRecipe recipe = new ShowRecipe();
                recipe.setId(c2.getInt(0));
                recipe.setUser(getUser(c2.getInt(1) ));
                recipe.setName(c2.getString(2));
                recipe.setRecipeText(c2.getString(3));
                recipe.setFatten(c2.getString(4));
                recipe.setImg(blobToBitmap(c2.getBlob(5)));
                getRecipeIngredient(recipe);
            ref_db.close();
            return recipe;
    }
    }*/

    /**
     * Test Recipe
     */
    public Recipe getRecipe() {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        ArrayList<RecipeIngredient> listIngredients = new ArrayList<>();
        Ingredient ingredient = null;
        Cursor c1 = ref_db.rawQuery("SELECT * FROM RECIPES WHERE ID = 2", null);
        int cant_reg = c1.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c1.moveToFirst();
            Recipe recipe = new Recipe();
            recipe.setName(c1.getString(2));
            recipe.setRecipeText(c1.getString(3));
            recipe.setFatten(c1.getString(4));
            recipe.setTypeofFood(c1.getString(5));
            recipe.setImg(blobToBitmap(c1.getBlob(6)));
            ref_db.close();
            return recipe;
        }
    }

    public Recipe getRecipe(int id) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        ArrayList<RecipeIngredient> listIngredients = new ArrayList<>();
        Cursor c1 = ref_db.rawQuery("SELECT * FROM RECIPES WHERE ID = ?", new String[]{String.valueOf(id)});
        int cant_reg = c1.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c1.moveToFirst();
            Recipe recipe = new Recipe();
            recipe.setId(c1.getInt(0));
            recipe.setName(c1.getString(2));
            recipe.setRecipeText(c1.getString(3));
            recipe.setFatten(c1.getString(4));
            recipe.setTypeofFood(c1.getString(5));
            recipe.setImg(blobToBitmap(c1.getBlob(6)));
            recipe.setRating(c1.getFloat(7));
            ref_db.close();
            return recipe;
        }

    }

    public Recipe getRecipeIntoTsR(int id) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        ArrayList<RecipeIngredient> listIngredients = new ArrayList<>();
        Cursor c1 = ref_db.rawQuery("SELECT * FROM RECIPES WHERE id = ?", new String[]{String.valueOf(id)});
        int cant_reg = c1.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c1.moveToFirst();
            Recipe recipe = new Recipe();
            recipe.setName(c1.getString(2));
            recipe.setRecipeText(c1.getString(3));
            recipe.setFatten(c1.getString(4));
            recipe.setTypeofFood(c1.getString(5));
            recipe.setImg(blobToBitmap(c1.getBlob(6)));

            Cursor c2 = ref_db.rawQuery("SELECT * FROM RECIPES_INGREDIENTS WHERE ID_RECIPE = ? ", new String[]{String.valueOf(id)});
            c2.moveToFirst();
            ArrayList<RecipeIngredient> recipeIngredients = new ArrayList<>();
            int amount = c2.getCount();
            c2.moveToFirst();
            for (int i = 0; i < amount; i++) {
                RecipeIngredient recipeIngredient = new RecipeIngredient();
                Ingredient ig = getIngredient(ref_db, c2.getInt(1));
                Log.v("ControllerDB", "Name-->" + ig.getName());

                recipeIngredient.setIngredient(ig);
                recipeIngredient.setAmount(c2.getInt(2));
                Log.v("ControllerDB", "Amount-->" + c2.getInt(2));

                recipeIngredients.add(recipeIngredient);
                c2.moveToNext();
            }
            recipe.addListIngredient(recipeIngredients);
            ref_db.close();
            return recipe;
        }
    }


    public ArrayList<Integer> getIdsRecipes(String breakFast) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT ID FROM RECIPES WHERE TYPEOFFOOD = ? ", new String[]{breakFast});
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            return null;
        } else {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                ids.add(c2.getInt(0));
                c2.moveToNext();
            }
            c2.close();
            ref_db.close();
            return ids;
        }
    }

    public int amountIds(String breakFast) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT ID FROM RECIPES WHERE TYPEOFFOOD = ? ", new String[]{breakFast});
        return c2.getCount();
    }

    public int returnIdUser(User user) {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c1 = ref_db.rawQuery("SELECT ID FROM USERS WHERE USER=? AND PASS=?", new String[]{user.getName(), user.getPass()});
        c1.moveToFirst();
        return c1.getInt(0);
    }

    public void updateRating(float actRating, float newRating, int idRecipe) {
        ContentValues cv = new ContentValues();
        cv.put("RATING", newRating);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update("RECIPES", cv, "RATING=? and ID=?", new String[]{String.valueOf(actRating), String.valueOf(idRecipe)});
    }

    public void getIdRecipes() {
        SQLiteDatabase ref_db = this.getReadableDatabase();
        Cursor c2 = ref_db.rawQuery("SELECT ID FROM RECIPES WHERE TYPEOFFOOD='Desayuno'", null);
        int cant_reg = c2.getCount();
        if (cant_reg == 0) {
            ref_db.close();
            Log.v("ControllerDB", "Something Wrong ");
        } else {
            c2.moveToFirst();
            for (int i = 0; i < cant_reg; i++) {
                Log.v("ControllerDB", "Recipe id --> " + c2.getInt(0));
                c2.moveToNext();
            }
            c2.close();
            ref_db.close();
        }
    }

}

