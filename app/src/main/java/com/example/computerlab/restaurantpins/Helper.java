package com.example.computerlab.restaurantpins;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by computerlab on 8/15/15.
 */
public class Helper extends SQLiteAssetHelper {

    public Helper(Context context) {
        super(context, "augmented.db", null, 1);
    }

    public Cursor getCursor() {
        SQLiteDatabase db = getReadableDatabase();
        //can say "_id, name" instead of "*", but _id must be included.
        Cursor cursor = db.rawQuery("SELECT * FROM restaurant_table;", null);
        //cursor.moveToFirst();
        return cursor;
    }
}
