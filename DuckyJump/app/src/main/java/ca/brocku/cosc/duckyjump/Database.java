package ca.brocku.cosc.duckyjump;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.brocku.cosc.duckyjump.GameObjects.SkinManager;

/**
 * Contains the high score information and the most recent duck skin that has been selected.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DB_NAME = "duckDB";
    public static final String DB_TABLE_SCORES = "scores";
    public static final String DB_TABLE_SKINS = "skins";
    private static final int SKIN_OFF = 0;
    public static final int SKIN_ON = 1;
    private static final String CREATE_TABLE_SCORES = "CREATE TABLE " + DB_TABLE_SCORES +
            " (rule INTEGER PRIMARY KEY, score INTEGER UNIQUE, date TEXT);";
    private static final String CREATE_TABLE_SKINS = "CREATE TABLE " + DB_TABLE_SKINS +
            " (rule INTEGER PRIMARY KEY, skinsID TEXT, selection INTEGER);";
    private static final String[] SCORES_FIELD = {"score", "date"}, SKINS_FIELD = {"skinsID", "selection"};
    private static final String SKINS_SELECTION = "selection=?", SKINS_ID = "skinsID=?";

    /**
     * Construct the database.
     * @param context   The context.
     */
    Database(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCORES);
        db.execSQL(CREATE_TABLE_SKINS);
    }

    /**
     * Save the specified string to the database.
     * @param value     The string to save.
     */
    public void saveScore(String value) {
        String[] data = new String[2];
        SQLiteDatabase db = this.getWritableDatabase();
        data[0] = value;
        data[1] = getTheDate();

        ContentValues values = new ContentValues();
        for (int i = 0; i < SCORES_FIELD.length; i++) {
            values.put(SCORES_FIELD[i], data[i]);
        }

        db.insertWithOnConflict(DB_TABLE_SCORES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    /**
     * Get the current skin that is in the database.
     * @return  The current skin in the database.
     */
    public SkinManager.Skin getSkin() {
        ArrayList<ArrayList<String>> skinID = query(Database.DB_TABLE_SKINS);
        for (int i = 0; i < skinID.size(); i++) {
            if (skinID.get(i).get(1).equals(Integer.toString(Database.SKIN_ON))) {
                String id = skinID.get(i).get(0);
                return SkinManager.Skin.valueOf(id);
            }
        }
        return SkinManager.Skin.YELLOW;
    }

    /**
     * Set a new skin to be saved in the database.
     * @param skin  The new skin to be saved in the database.
     */
    public void updateSkin(String skin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(SKINS_FIELD[1], SKIN_ON);
        resetSkins(db);
        if (db.update(DB_TABLE_SKINS, content, SKINS_ID, new String[]{skin}) == 0) {
            content.put(SKINS_FIELD[0], skin);
            db.insert(DB_TABLE_SKINS, null, content);
        }
        db.close();
    }

    /**
     * Set all the skins to 0. This might come in useful in the future where the user can "unlock"
     * skins.
     * @param db    The database to reset the skins in.
     */
    private void resetSkins(SQLiteDatabase db) {
        ContentValues content = new ContentValues();
        content.put(SKINS_FIELD[1], SKIN_OFF);
        db.update(DB_TABLE_SKINS, content, SKINS_SELECTION, new String[]{Integer.toString(SKIN_ON)});
    }

    /**
     * Fill spinner and list views.
     * @param table     The table to perform on.
     */
    public ArrayList<ArrayList<String>> query(String table) {
        String[] fields = SCORES_FIELD;
        if (table.equals(DB_TABLE_SKINS)) fields = SKINS_FIELD;
        ArrayList<ArrayList<String>> entries = new ArrayList<>();
        SQLiteDatabase datareader = this.getReadableDatabase();
        String column = null;
        if (table.equals(DB_TABLE_SCORES)) column = SCORES_FIELD[0] + " DESC";
        Cursor cursor = datareader.query(table, fields, null, null, null, null, column);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ArrayList<String> entry = new ArrayList<>();
            if (table.equals(DB_TABLE_SCORES)) {
                entry.add(cursor.getString(0));
                entry.add(cursor.getString(1));
                entries.add(entry);
            } else if (table.equals(DB_TABLE_SKINS)) {
                entry.add(cursor.getString(0));
                entry.add(Integer.toString(cursor.getInt(1)));
                entries.add(entry);
            }
            cursor.moveToNext();
        }
        datareader.close();
        if (cursor != null && !cursor.isClosed()) cursor.close();

        return entries;
    }

    /**
     * This deletes from the table based on the params.
     * @param table     The specified table.
     * @param id        The specified id.
     * @param id_names  The spcified id names.
     */
    public void delete(String table, String id, String[] id_names) {
        SQLiteDatabase data = this.getWritableDatabase();
        data.delete(table, id, id_names);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.needUpgrade(newVersion);
    }

    public int getHighScore() {
        ArrayList<ArrayList<String>> scores = query(DB_TABLE_SCORES);
        int highScore = 0;
        if (scores.size() > 0) {
            int score = Integer.parseInt(scores.get(0).get(0));
            if (score > highScore) {
                highScore = score;
            }
        }
        return highScore;
    }

    /**
     * Get the current date.
     * @return  The current date.
     */
    private String getTheDate() {
        Date d = Calendar.getInstance().getTime();
        return String.format(Locale.CANADA, " %tD", d);
    }
}