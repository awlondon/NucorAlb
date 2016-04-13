package com.example.anticorruption;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexlondon on 2/11/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public static final String DATABASE_NAME = "adv_data.db";

    public static final int DATABASE_VERSION = 1;

    public static final UniversalMethodsAndVariables um = new UniversalMethodsAndVariables();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create tables
        db.execSQL("CREATE TABLE " + InstitutionTable.TABLE_NAME + " ( "
                + InstitutionTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + InstitutionTable.INSTITUTION + " TEXT , "
                + InstitutionTable.ADDRESS + " TEXT , "
                + InstitutionTable.TYPE + " TEXT , "
                + InstitutionTable.CITY + " TEXT , "
                + InstitutionTable.LOGO + " TEXT , "
                + InstitutionTable.MANAGER + " TEXT , "
                + InstitutionTable.PHONE + " TEXT , "
                + InstitutionTable.VIEWS + " TEXT , "
                + InstitutionTable.ABUSE_OF_DISCRETION + " TEXT , "
                + InstitutionTable.BLACKMAIL + " TEXT , "
                + InstitutionTable.BRIBERY + " TEXT , "
                + InstitutionTable.EMBEZZLEMENT + " TEXT , "
                + InstitutionTable.EXTORTION + " TEXT , "
                + InstitutionTable.FRAUD + " TEXT , "
                + InstitutionTable.NEPOTISM + " TEXT , "
                + InstitutionTable.OTHER_CORRUPTION + " TEXT , "
                + InstitutionTable.APPOINTMENT_WAIT_TIME + " TEXT , "
                + InstitutionTable.APPOINTMENT_ENTRIES + " TEXT , "
                + InstitutionTable.DOCUMENT_WAIT_TIME + " TEXT , "
                + InstitutionTable.DOCUMENT_ENTRIES + " TEXT , "
                + InstitutionTable.PERMIT_WAIT_TIME + " TEXT , "
                + InstitutionTable.PERMIT_ENTRIES + " TEXT , "
                + InstitutionTable.POSITIVE + " TEXT , "
                + InstitutionTable.NEGATIVE + " TEXT );");

        db.execSQL("CREATE TABLE " + StoryTable.TABLE_NAME + " ( "
                + StoryTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + StoryTable.CONTENT + " TEXT , "
                + StoryTable.INSTITUTION + " TEXT , "
                + StoryTable.TYPE + " TEXT , "
                + StoryTable.VIEWS + " TEXT , "
                + StoryTable.DATE + " TEXT );");

        db.execSQL("CREATE TABLE " + PostTable.TABLE_NAME + " ( "
                + PostTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + PostTable.USERNAME + " TEXT , "
                + PostTable.THREAD + " TEXT , "
                + PostTable.PREVIOUS + " TEXT , "
                + PostTable.TITLE + " TEXT , "
                + PostTable.CONTENT + " TEXT , "
                + PostTable.UP_VOTES + " INTEGER , "
                + PostTable.DOWN_VOTES + " INTEGER , "
                + PostTable.VIEWS + " INTEGER , "
                + PostTable.DATE + " TEXT );");

        db.execSQL("CREATE TABLE " + WikiTable.TABLE_NAME + " ( "
                + WikiTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + WikiTable.CATEGORY + " TEXT , "
                + WikiTable.TAGS + " TEXT , "
                + WikiTable.TITLE + " TEXT , "
                + WikiTable.CONTENT + " TEXT , "
                + WikiTable.VIEWS + " TEXT , "
                + WikiTable.CREATED + " TEXT , "
                + WikiTable.UPDATED + " TEXT );");

        db.execSQL("CREATE TABLE " + UserTable.TABLE_NAME + " ( "
                + UserTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
                + UserTable.USERNAME + " TEXT , "
                + UserTable.PASSWORD + " TEXT , "
                + UserTable.ICON + " TEXT , "
                + UserTable.FIRST_NAME + " TEXT , "
                + UserTable.LAST_NAME + " TEXT , "
                + UserTable.LOCATION + " TEXT , "
                + UserTable.EMAIL + " TEXT ,"
                + UserTable.INSTITUTIONS_FOLLOWING + " TEXT , "
                + UserTable.WIKIS_FOLLOWING + " TEXT , "
                + UserTable.POSTS_FOLLOWING + " TEXT , "
                + UserTable.SURVEYS_FINISHED + " TEXT , "
                + UserTable.POSTS_POSITIVE + " TEXT , "
                + UserTable.POSTS_NEGATIVE + " TEXT , "
                + UserTable.ENROLLMENT + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w("LOG_TAG", "Upgrading database from version" + oldVersion + " to " +
                newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + InstitutionTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StoryTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PostTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WikiTable.TABLE_NAME);

        onCreate(db);

    }

    public long addInstitution(String institution, String address, String city, String type,
                               String logo, String manager, String phone) {

        ContentValues cv = new ContentValues();
        cv.put(InstitutionTable.INSTITUTION, institution);
        cv.put(InstitutionTable.ADDRESS, address);
        cv.put(InstitutionTable.CITY, city);
        cv.put(InstitutionTable.TYPE, type);
        cv.put(InstitutionTable.LOGO, logo);
        cv.put(InstitutionTable.MANAGER, manager);
        cv.put(InstitutionTable.PHONE, phone);
        cv.put(InstitutionTable.ABUSE_OF_DISCRETION, 0);
        cv.put(InstitutionTable.BLACKMAIL, 0);
        cv.put(InstitutionTable.BRIBERY, 0);
        cv.put(InstitutionTable.EMBEZZLEMENT, 0);
        cv.put(InstitutionTable.EXTORTION, 0);
        cv.put(InstitutionTable.FRAUD, 0);
        cv.put(InstitutionTable.NEPOTISM, 0);
        cv.put(InstitutionTable.OTHER_CORRUPTION, 0);
        cv.put(InstitutionTable.APPOINTMENT_WAIT_TIME, (byte[]) null);
        cv.put(InstitutionTable.APPOINTMENT_ENTRIES, 0);
        cv.put(InstitutionTable.DOCUMENT_WAIT_TIME, (byte[]) null);
        cv.put(InstitutionTable.DOCUMENT_ENTRIES, 0);
        cv.put(InstitutionTable.PERMIT_WAIT_TIME, (byte[]) null);
        cv.put(InstitutionTable.PERMIT_ENTRIES, 0);
        cv.put(InstitutionTable.POSITIVE, 0);
        cv.put(InstitutionTable.NEGATIVE, 0);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(InstitutionTable.TABLE_NAME, InstitutionTable.INSTITUTION, cv);
        db.releaseReference();
        return result;
    }

    public long addWiki(String title, String content, String category, String tags,
                        String created) {

        ContentValues cv = new ContentValues();
        cv.put(WikiTable.TITLE, title);
        cv.put(WikiTable.CONTENT, content);
        cv.put(WikiTable.CATEGORY, category);
        cv.put(WikiTable.TAGS, tags);
        cv.put(WikiTable.CREATED, created);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(WikiTable.TABLE_NAME, WikiTable.TITLE, cv);
        
        return result;
    }

    public long addPost(String title, String content, String username,
                        String thread, String previous, int thumbs_up, int thumbs_down, String date) {

        ContentValues cv = new ContentValues();
        cv.put(PostTable.TITLE, title);
        cv.put(PostTable.CONTENT, content);
        cv.put(PostTable.USERNAME, username);
        cv.put(PostTable.THREAD, thread);
        cv.put(PostTable.PREVIOUS, previous);
        cv.put(PostTable.UP_VOTES, thumbs_up);
        cv.put(PostTable.DOWN_VOTES, thumbs_down);
        cv.put(PostTable.DATE, date);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(PostTable.TABLE_NAME, PostTable.TITLE, cv);
        
        return result;
    }

    public long addStory(String content, String institution, String type, String date) {
        if (content.equals("(Select a type)"))
            content = "Other";
        ContentValues cv = new ContentValues();
        cv.put(StoryTable.CONTENT, content);
        cv.put(StoryTable.INSTITUTION, institution);
        cv.put(StoryTable.TYPE, type);
        cv.put(StoryTable.DATE, date);

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.insert(StoryTable.TABLE_NAME, null, cv);
        
        return result;
    }

    public long addUser(String username, String password, String first_name, String last_name,
                        String icon, String location, String email, String enrollment) {
        ContentValues cv = new ContentValues();
        cv.put(UserTable.USERNAME, username);
        cv.put(UserTable.PASSWORD, password);
        cv.put(UserTable.FIRST_NAME, first_name);
        cv.put(UserTable.LAST_NAME, last_name);
        cv.put(UserTable.ICON, icon);
        cv.put(UserTable.LOCATION, location);
        cv.put(UserTable.EMAIL, email);
        cv.put(UserTable.ENROLLMENT, enrollment);

        SQLiteDatabase db = getWritableDatabase();
        long result = db.insert(UserTable.TABLE_NAME, null, cv);
        
        return result;
    }


    public String[] getStringArray(String table, String column, String[] startingStrings, boolean split) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{column};
        Cursor c = db.query(table, col, null, null, null, null, null);
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> disposableList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            if (startingStrings != null) {
                Collections.addAll(arrayList, startingStrings);
            }
            if (c.moveToFirst()) {
                do {
                    String string = c.getString(0).trim();
                    if (!split && !disposableList.contains(string)) {
                        disposableList.add(string);
                    } else {
                        String[] stringArray = string.split(";");
                        for (String aStringArray : stringArray) {
                            if (!disposableList.contains(aStringArray)) {
                                disposableList.add(aStringArray);
                            }
                        }
                    }
                } while (c.moveToNext());
            }
            c.close();
        }
        Collections.sort(disposableList);
        arrayList.addAll(disposableList);
        
        return arrayList.toArray(new String[arrayList.size()]);
    }

    public Integer[] getStoriesByQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{StoryTable.ID, StoryTable.CONTENT, StoryTable.INSTITUTION, StoryTable.TYPE};
        Cursor c = db.query(StoryTable.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<String> foundStoryIds = parseSelectionByQuery(query, c, StoryTable.ID);
        c.close();

        Integer[] result = new Integer[foundStoryIds.size()];
        for (int i = 0; i < foundStoryIds.size(); i++) {
            result[i] = Integer.valueOf(foundStoryIds.get(i));
        }
        return result;
    }

    public Integer[] getWikisByQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{WikiTable.ID, WikiTable.CONTENT, WikiTable.TAGS, WikiTable.CATEGORY};
        Cursor c = db.query(WikiTable.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<String> foundWikiIds = parseSelectionByQuery(query, c, WikiTable.ID);
        c.close();

        Integer[] result = new Integer[foundWikiIds.size()];
        for (int i = 0; i < foundWikiIds.size(); i++) {
            result[i] = Integer.valueOf(foundWikiIds.get(i));
        }
        return result;
    }

    public Integer[] getPostsByQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{PostTable.ID, PostTable.CONTENT, PostTable.THREAD, PostTable.TITLE};
        Cursor c = db.query(PostTable.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<String> foundPostIds = parseSelectionByQuery(query, c, PostTable.ID);
        c.close();
        
        Integer[] result = new Integer[foundPostIds.size()];
        for (int i = 0; i < foundPostIds.size(); i++) {
            result[i] = Integer.valueOf(foundPostIds.get(i));
        }
        return result;
    }

    public Integer[] getInstitutionsByQuery(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{InstitutionTable.ID, InstitutionTable.ADDRESS, InstitutionTable.CITY, InstitutionTable.MANAGER, InstitutionTable.INSTITUTION, InstitutionTable.TYPE};
        Cursor c = db.query(InstitutionTable.TABLE_NAME, cols, null, null, null, null, null);
        ArrayList<String> foundInstitutionIds = parseSelectionByQuery(query, c, InstitutionTable.ID);
        c.close();
        
        Bundle[] idsAndNames = new Bundle[foundInstitutionIds.size()];
        for (int i = 0; i < foundInstitutionIds.size(); i++) {
            idsAndNames[i] = getInstitutionIdForOrdering(InstitutionTable.ID, foundInstitutionIds.get(i));
        }
        HashMap<String, Integer> mHashMap = new HashMap<>();
        for (Bundle bundle : idsAndNames) {
            String string = bundle.getString("INSTITUTION");
            Integer integer = bundle.getInt("ID");
            mHashMap.put(string, integer);
        }
        ArrayList<String> mArrayListInstitutions = new ArrayList<>();
        mArrayListInstitutions.addAll(mHashMap.keySet());
        Collections.sort(mArrayListInstitutions);
        Integer[] result = new Integer[mArrayListInstitutions.size()];
        for (int i = 0; i < mArrayListInstitutions.size(); i++) {
            result[i] = mHashMap.get(mArrayListInstitutions.get(i));
        }
        return result;
    }

    public ArrayList<String> parseSelectionByQuery(String query, Cursor c, String table_id) {
        ArrayList<String> foundIds = new ArrayList<>();
        if (query != null) {
            query = query.toLowerCase();
            if (c != null && c.moveToFirst()) {
                do {
                    ArrayList<Integer> countedArray = new ArrayList<>();
                    String[] querySplit = query.split("[,\\s\\-:\\?]");
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        String string = c.getString(i);
                        if (string != null) {
                            string = string.toLowerCase();
                            for (int n = 0; n < querySplit.length; n++) {
                                Pattern pattern = Pattern.compile(querySplit[n]);
                                Matcher matcher = pattern.matcher(string);
                                if (matcher.find()) {
                                    if (!countedArray.contains(n)) {
                                        countedArray.add(n);
                                        System.out.println("Matches: " + countedArray);
                                    }
                                }
                            }
                        }

                        if (countedArray.size() == querySplit.length) {
                            if (!foundIds.contains(String.valueOf(c.getInt(c.getColumnIndexOrThrow(table_id)))))
                                foundIds.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(table_id))));
                        }
                    }
                } while (c.moveToNext());
            }
        } else {
            if (c != null && c.moveToFirst()) {
                do {
                    System.out.println("Adding id...");
                    foundIds.add(String.valueOf(c.getInt(c.getColumnIndexOrThrow(table_id))));
                } while (c.moveToNext());
            }
        }
        System.out.println("Queried ids returned..." + foundIds);
        return foundIds;
    }

    public Bundle getInstitutionIdForOrdering(String col, String args){
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{args};
        Cursor c = db.query(InstitutionTable.TABLE_NAME, null, col + "= ?", selectionArgs, null, null, null);
        Bundle result;
        if ((c != null) && c.moveToFirst()){
            result = new Bundle();
            result.putInt("ID", c.getInt(c.getColumnIndexOrThrow(InstitutionTable.ID)));
            result.putString("INSTITUTION", c.getString(c.getColumnIndexOrThrow(InstitutionTable.INSTITUTION)));
            c.close();
            return result;
        }
        return null;
    }

    public Bundle getInstitution(String col, String args) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{args};
        Cursor c = db.query(InstitutionTable.TABLE_NAME, null, col + "= ?", selectionArgs, null, null, null);
        Bundle result;
        if (c != null && c.moveToFirst()) {
            result = new Bundle();
            result.putString("ID", String.valueOf(c.getInt(c.getColumnIndexOrThrow(InstitutionTable.ID))));
            result.putString("TABLE_NAME", InstitutionTable.TABLE_NAME);
            result.putString("INSTITUTION", c.getString(c.getColumnIndexOrThrow(InstitutionTable.INSTITUTION)));
            result.putString("ADDRESS", c.getString(c.getColumnIndexOrThrow(InstitutionTable.ADDRESS)));
            result.putString("CITY", c.getString(c.getColumnIndexOrThrow(InstitutionTable.CITY)));
            result.putString("TYPE", c.getString(c.getColumnIndexOrThrow(InstitutionTable.TYPE)));
            result.putString("LOGO", c.getString(c.getColumnIndexOrThrow(InstitutionTable.LOGO)));
            result.putString("PHONE", c.getString(c.getColumnIndexOrThrow(InstitutionTable.PHONE)));
            result.putString("MANAGER", c.getString(c.getColumnIndexOrThrow(InstitutionTable.MANAGER)));
            result.putString("VIEWS", c.getString(c.getColumnIndexOrThrow(InstitutionTable.VIEWS)));
            result.putString("POSITIVE", c.getString(c.getColumnIndexOrThrow(InstitutionTable.POSITIVE)));
            result.putString("NEGATIVE", c.getString(c.getColumnIndexOrThrow(InstitutionTable.NEGATIVE)));
            result.putString("ABUSE_OF_DISCRETION", c.getString(c.getColumnIndexOrThrow(InstitutionTable.ABUSE_OF_DISCRETION)));
            result.putString("BLACKMAIL", c.getString(c.getColumnIndexOrThrow(InstitutionTable.BLACKMAIL)));
            result.putString("BRIBERY", c.getString(c.getColumnIndexOrThrow(InstitutionTable.BRIBERY)));
            result.putString("EMBEZZLEMENT", c.getString(c.getColumnIndexOrThrow(InstitutionTable.EMBEZZLEMENT)));
            result.putString("EXTORTION", c.getString(c.getColumnIndexOrThrow(InstitutionTable.EXTORTION)));
            result.putString("FRAUD", c.getString(c.getColumnIndexOrThrow(InstitutionTable.FRAUD)));
            result.putString("NEPOTISM", c.getString(c.getColumnIndexOrThrow(InstitutionTable.NEPOTISM)));
            result.putString("OTHER_CORRUPTION", c.getString(c.getColumnIndexOrThrow(InstitutionTable.OTHER_CORRUPTION)));
            result.putString("APPOINTMENT_WAIT_TIME", c.getString(c.getColumnIndexOrThrow(InstitutionTable.APPOINTMENT_WAIT_TIME)));
            result.putString("DOCUMENT_WAIT_TIME", c.getString(c.getColumnIndexOrThrow(InstitutionTable.DOCUMENT_WAIT_TIME)));
            result.putString("PERMIT_WAIT_TIME", c.getString(c.getColumnIndexOrThrow(InstitutionTable.PERMIT_WAIT_TIME)));

            System.out.println("Institution: returns Bundle");
            c.close();
            
            return result;
        }
        System.out.println("Institution: returns null");
        
        return null;
    }

    public long updateInstitutionCorruptionType(Integer id, String type) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(id)};
        String[] col = new String[]{type};
        Cursor c = db.query(InstitutionTable.TABLE_NAME, col, InstitutionTable.ID + "=?", whereArgs, null, null, null);
        Integer value = null;
        if (c.moveToFirst()) {
            value = Integer.valueOf(c.getString(0));
            value++;
        }
        c.close();
        ContentValues cv = new ContentValues();
        cv.put(type, String.valueOf(value));
        long result = db.update(InstitutionTable.TABLE_NAME, cv, InstitutionTable.ID + "=?", whereArgs);
        
        return result;
    }

    public long updateInstitutionRating(Integer id, String type, Integer value) {
        SQLiteDatabase wd = getWritableDatabase();
        String[] col = new String[]{type};
        String[] whereArgs = new String[]{String.valueOf(id)};
        Cursor c = wd.query(InstitutionTable.TABLE_NAME, col, InstitutionTable.ID + "=?", whereArgs, null, null, null);
        Integer total = null;
        if (c.moveToFirst()) {
            total = Integer.valueOf(c.getString(0));
            c.close();
            total += value;
        }
        ContentValues cv = new ContentValues();
        cv.put(type, String.valueOf(total));
        long result = wd.update(InstitutionTable.TABLE_NAME, cv, InstitutionTable.ID + "=?", whereArgs);
        
        return result;
    }

    public long updateInstitutionWaitTime(Integer id, String wait_type, String wait_entries, HashMap<String, Integer> map, String newValue) {
        HashMap<Integer, String> reverseMap = new HashMap<>();
        for (String string : map.keySet()) {
            reverseMap.put(map.get(string), string);
        }
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{wait_type, wait_entries};
        String[] whereArgs = new String[]{String.valueOf(id)};
        Cursor c = db.query(InstitutionTable.TABLE_NAME, cols, InstitutionTable.ID + "=?", whereArgs, null, null, null);
        if (c.moveToFirst()) {
            Integer numEntries = 0;
            if (c.getString(1) != null)
                numEntries = Integer.valueOf(c.getString(1));
            Integer average = 0;
            if (c.getString(0) != null)
                average = map.get(c.getString(0));
            c.close();
            
            Integer total = numEntries * average;
            total += map.get(newValue);
            numEntries++;
            double newTotal = total / numEntries;
            if (total % numEntries > .5)
                average = (int) Math.ceil(newTotal);
            else
                average = (int) Math.floor(newTotal);

            ContentValues cv = new ContentValues();
            cv.put(wait_type, reverseMap.get(average));
            cv.put(wait_entries, numEntries);
            SQLiteDatabase wd = getWritableDatabase();
            long result = wd.update(InstitutionTable.TABLE_NAME, cv, InstitutionTable.ID + "=?", whereArgs);
            
            return result;
        }
        return -1;
    }

    public long updateUserIcon(String username, String icon) {
        SQLiteDatabase wd = getWritableDatabase();
        String[] whereArgs = new String[]{username};

        ContentValues cv = new ContentValues();
        cv.put(UserTable.ICON, icon);

        long result = wd.update(UserTable.TABLE_NAME, cv, UserTable.USERNAME + "=?", whereArgs);
        return result;
    }

    public Bundle getPost(String col, String args) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{args};
        Cursor c = db.query(PostTable.TABLE_NAME, null, col + "= ?", selectionArgs, null, null, null);
        Bundle result;
        if (c != null && c.moveToFirst()) {
            result = new Bundle();
            result.putInt("ID", c.getInt(c.getColumnIndexOrThrow(PostTable.ID)));
            result.putString("TABLE_NAME", PostTable.TABLE_NAME);
            result.putString("TITLE", c.getString(c.getColumnIndexOrThrow(PostTable.TITLE)));
            result.putString("CONTENT", c.getString(c.getColumnIndexOrThrow(PostTable.CONTENT)));
            result.putString("USERNAME", c.getString(c.getColumnIndexOrThrow(PostTable.USERNAME)));
            result.putString("THREAD", c.getString(c.getColumnIndexOrThrow(PostTable.THREAD)));
            result.putString("PREVIOUS", c.getString(c.getColumnIndexOrThrow(PostTable.PREVIOUS)));
            result.putString("UP_VOTES", c.getString(c.getColumnIndexOrThrow(PostTable.UP_VOTES)));
            result.putString("DOWN_VOTES", c.getString(c.getColumnIndexOrThrow(PostTable.DOWN_VOTES)));
            result.putString("VIEWS", c.getString(c.getColumnIndexOrThrow(PostTable.VIEWS)));
            result.putString("DATE", c.getString(c.getColumnIndexOrThrow(PostTable.DATE)));
            c.close();
            
            System.out.println("Post: returns Bundle");
            return result;
        }
        System.out.println("Post: returns null");
        
        return null;
    }

    public Bundle getLatestPost() {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{PostTable.ID};
        Cursor c = db.query(PostTable.TABLE_NAME, col, null, null, null, null, null);
        ArrayList<Integer> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                list.add(Integer.valueOf(c.getString(0)));
            } while (c.moveToNext());
        }
        c.close();
        
        return getPost(PostTable.ID, String.valueOf(Collections.max(list)));
    }

    public long updatePost(int id, String title, String content) {
        ContentValues cv = new ContentValues();
        cv.put(PostTable.TITLE, title);
        cv.put(PostTable.CONTENT, content);
        cv.put(PostTable.DATE, String.valueOf(new java.sql.Timestamp(new Date().getTime())));

        String[] whereArgs = new String[]{String.valueOf(id)};

        SQLiteDatabase db = getWritableDatabase();
        long result = db.update(PostTable.TABLE_NAME, cv, PostTable.ID + "=?", whereArgs);
        
        return result;
    }

    public long updateWiki(int id, String title, String category, String content, String tags, String updated) {
        ContentValues cv = new ContentValues();
        cv.put(WikiTable.ID, id);
        cv.put(WikiTable.TITLE, title);
        cv.put(WikiTable.CATEGORY, category);
        cv.put(WikiTable.CONTENT, content);
        cv.put(WikiTable.TAGS, tags);
        cv.put(WikiTable.UPDATED, updated);

        String[] whereArgs = new String[]{String.valueOf(id)};

        SQLiteDatabase db = getWritableDatabase();
        long result = db.update(WikiTable.TABLE_NAME, cv, WikiTable.ID + "=?", whereArgs);
        
        return result;
    }

    public Bundle getStory(String col, String args) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{args};
        Cursor c = db.query(StoryTable.TABLE_NAME, null, col + "= ?", selectionArgs, null, null, null);
        Bundle result;
        if (c != null && c.moveToFirst()) {
            result = new Bundle();
            result.putString("ID", String.valueOf(c.getInt(c.getColumnIndexOrThrow(StoryTable.ID))));
            result.putString("TABLE_NAME", StoryTable.TABLE_NAME);
            result.putString("CONTENT", c.getString(c.getColumnIndexOrThrow(StoryTable.CONTENT)));
            result.putString("INSTITUTION", c.getString(c.getColumnIndexOrThrow(StoryTable.INSTITUTION)));
            result.putString("VIEWS", c.getString(c.getColumnIndexOrThrow(StoryTable.VIEWS)));
            result.putString("TYPE", c.getString(c.getColumnIndexOrThrow(StoryTable.TYPE)));
            result.putString("DATE", c.getString(c.getColumnIndexOrThrow(StoryTable.DATE)));
            c.close();
            
            System.out.println("Story: returns Bundle");
            return result;
        }
        System.out.println("Story: returns null");
        
        return null;
    }

    public Bundle getWiki(String col, String args) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{args};
        Cursor c = db.query(WikiTable.TABLE_NAME, null, col + "= ?", selectionArgs, null, null, null);
        Bundle result;
        if (c != null && c.moveToFirst()) {
            result = new Bundle();
            result.putString("ID", String.valueOf(c.getInt(c.getColumnIndexOrThrow(WikiTable.ID))));
            result.putString("TABLE_NAME", WikiTable.TABLE_NAME);
            result.putString("TITLE", c.getString(c.getColumnIndexOrThrow(WikiTable.TITLE)));
            result.putString("CONTENT", c.getString(c.getColumnIndexOrThrow(WikiTable.CONTENT)));
            result.putString("CATEGORY", c.getString(c.getColumnIndexOrThrow(WikiTable.CATEGORY)));
            result.putString("TAGS", c.getString(c.getColumnIndexOrThrow(WikiTable.TAGS)));
            result.putString("VIEWS", c.getString(c.getColumnIndexOrThrow(WikiTable.VIEWS)));
            result.putString("CREATED", c.getString(c.getColumnIndexOrThrow(WikiTable.CREATED)));
            result.putString("UPDATED", c.getString(c.getColumnIndexOrThrow(WikiTable.UPDATED)));
            c.close();
            
            System.out.println("Wiki: returns Bundle");
            return result;
        }
        System.out.println("Wiki: returns null");
        
        return null;
    }

    public Bundle getUser(String col, String args) {
        SQLiteDatabase db = getReadableDatabase();
        String[] selectionArgs = new String[]{args};
        Cursor  c = db.query(UserTable.TABLE_NAME, null, col + "= ?", selectionArgs, null, null, null);
        Bundle result;
        if (c != null && c.moveToFirst()) {
            result = new Bundle();
            result.putString("ID", String.valueOf(c.getInt(c.getColumnIndexOrThrow(UserTable.ID))));
            result.putString("TABLE_NAME", UserTable.TABLE_NAME);
            result.putString("USERNAME", c.getString(c.getColumnIndexOrThrow(UserTable.USERNAME)));
            result.putString("PASSWORD", c.getString(c.getColumnIndexOrThrow(UserTable.PASSWORD)));
            result.putString("ICON", c.getString(c.getColumnIndexOrThrow(UserTable.ICON)));
            result.putString("FIRST_NAME", c.getString(c.getColumnIndexOrThrow(UserTable.FIRST_NAME)));
            result.putString("LAST_NAME", c.getString(c.getColumnIndexOrThrow(UserTable.LAST_NAME)));
            result.putString("LOCATION", c.getString(c.getColumnIndexOrThrow(UserTable.LOCATION)));
            result.putString("EMAIL", c.getString(c.getColumnIndexOrThrow(UserTable.EMAIL)));
            result.putString("INSTITUTIONS_FOLLOWING", c.getString(c.getColumnIndexOrThrow(UserTable.INSTITUTIONS_FOLLOWING)));
            result.putString("WIKIS_FOLLOWING", c.getString(c.getColumnIndexOrThrow(UserTable.WIKIS_FOLLOWING)));
            result.putString("POSTS_FOLLOWING", c.getString(c.getColumnIndexOrThrow(UserTable.POSTS_FOLLOWING)));
            result.putString("POSTS_POSITIVE", c.getString(c.getColumnIndexOrThrow(UserTable.POSTS_POSITIVE)));
            result.putString("POSTS_NEGATIVE", c.getString(c.getColumnIndexOrThrow(UserTable.POSTS_NEGATIVE)));
            result.putString("ENROLLMENT", c.getString(c.getColumnIndexOrThrow(UserTable.ENROLLMENT)));
            c.close();
            
            System.out.println("User: returns Bundle");
            return result;
        }
        System.out.println("User: returns null");
        
        return null;
    }

    public long updateUserFinishedSurveys(String username, Integer id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{UserTable.SURVEYS_FINISHED};
        String[] whereArgs = new String[]{username};
        Cursor c = db.query(UserTable.TABLE_NAME, col, UserTable.USERNAME + "=?", whereArgs, null, null, null);
        if (c.moveToFirst()) {
            String ids = c.getString(0);
            c.close();
            
            if (ids != null)
                ids += String.valueOf(id) + ";";
            else
                ids = String.valueOf(id) + ";";
            ContentValues cv = new ContentValues();
            cv.put(UserTable.SURVEYS_FINISHED, ids);
            SQLiteDatabase wd = getWritableDatabase();
            long result = wd.update(UserTable.TABLE_NAME, cv, UserTable.USERNAME + "=?", whereArgs);
            
            return result;
        }
        return -1;
    }

    public boolean isInFinishedSurveys(String username, Integer id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{UserTable.SURVEYS_FINISHED};
        String[] whereArgs = new String[]{username};
        Cursor c = db.query(UserTable.TABLE_NAME, col, UserTable.USERNAME + "=?", whereArgs, null, null, null);
        if (c.moveToFirst()) {
            if (c.getString(0) != null) {
                String[] ids = c.getString(0).split(";");
                c.close();
                
                for (String id1 : ids) {
                    if (Integer.valueOf(id1).equals(id))
                        return true;
                }
            }
            return false;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public int getPositionOfPost(String thread, String targetId) {
        ArrayList<String> threadPosts = organizePostsForReplies(getPostsByThread(thread));
        for (int i = 0; i < threadPosts.size(); i++) {
            if (Objects.equals(threadPosts.get(i), targetId)) {
                return i;
            }
        }
        return 0;
    }

    public ArrayList<String> organizePostsByTimestamp(ArrayList<String> idsArrayList) {
        ArrayList<Timestamp> orderedTimestamps = new ArrayList<>();
        ArrayList<String> newIdsArray = new ArrayList<>();
        for (int i = idsArrayList.size() - 1; i > -1; i--) {
            Bundle bundle = getPost(PostTable.ID, idsArrayList.get(i));
            String timestampString = bundle.getString("DATE");
            if (timestampString != null) {
                Timestamp timestamp = Timestamp.valueOf(timestampString);
                orderedTimestamps.add(timestamp);
                Collections.sort(orderedTimestamps);
                newIdsArray.add(orderedTimestamps.indexOf(timestamp), idsArrayList.get(i));
            } else {
                newIdsArray.add(idsArrayList.get(i));
            }
        }
        return newIdsArray;
    }

    public ArrayList<String> getPostsByThread(String thread) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{PostTable.ID};
        String[] selectionArgs = new String[]{thread};
        Cursor c = db.query(PostTable.TABLE_NAME, cols, PostTable.THREAD + "= ?", selectionArgs, null, null, null);
        ArrayList<String> result = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    result.add(String.valueOf(c.getInt(0)));
                } while (c.moveToNext());
            }
            c.close();
            
            Collections.sort(result);
            return result;
        }
        return null;
    }

    public ArrayList<String> organizePostsForReplies(ArrayList<String> idsArray) {
        ArrayList<String> newIdsArray = new ArrayList<>();
        for (String id : idsArray) {
            Bundle bundle = getPost(PostTable.ID, id);
            String previous = bundle.getString("PREVIOUS");
            if (previous == null) {
                newIdsArray.add(id);
            }
        }
        do {
            for (String id : idsArray) {
                Bundle bundle = getPost(PostTable.ID, id);
                String previous = bundle.getString("PREVIOUS");
                if (newIdsArray.contains(previous) && !newIdsArray.contains(id)) {
                    newIdsArray.add(newIdsArray.indexOf(previous) + 1, id);
                }
            }
            System.out.println("Passed through 1 ArrayList iteration");
        } while (!newIdsArray.containsAll(idsArray));
        return newIdsArray;
    }

    public Bundle[] getPostsByThreadAndMostRecent(String thread) {
        ArrayList<String> threadPosts = getPostsByThread(thread);
        if (threadPosts != null) {
            threadPosts = organizePostsByTimestamp(threadPosts);
            Bundle[] result = new Bundle[threadPosts.size()];
            for (int i = 0; i < threadPosts.size(); i++) {
                result[i] = getPost(PostTable.ID, threadPosts.get(i));
            }
            return result;
        }
        return null;
    }

    public Bundle[] getPostsByThreadAndReplies(String thread) {
        ArrayList<String> threadPosts = getPostsByThread(thread);
        threadPosts = organizePostsForReplies(threadPosts);
        if (threadPosts != null) {
            Bundle[] result = new Bundle[threadPosts.size()];
            for (int i = 0; i < threadPosts.size(); i++) {
                result[i] = getPost(PostTable.ID, threadPosts.get(i));
            }
            return result;
        }
        return null;
    }

    public boolean removeInstitution(int institutionId) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(institutionId)};

        int result = db.delete(InstitutionTable.TABLE_NAME, InstitutionTable.ID + "= ?", whereArgs);
        
        return (result > 0);
    }

    public boolean removeWiki(int wikiId) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(wikiId)};

        int result = db.delete(WikiTable.TABLE_NAME, WikiTable.ID + "= ?", whereArgs);
        
        return (result > 0);
    }

    public boolean removeStory(int storyId) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(storyId)};

        int result = db.delete(StoryTable.TABLE_NAME, StoryTable.ID + "= ?", whereArgs);
        
        return (result > 0);
    }

    public boolean removePost(int postId) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(postId)};

        int result = db.delete(PostTable.TABLE_NAME, PostTable.ID + "= ?", whereArgs);
        
        return (result > 0);
    }

    public boolean isInTable(String table, String col, String query) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{col};
        Cursor c = db.query(table, cols, null, null, null, null, null);
        query = query.toLowerCase().trim();
        if (c.moveToFirst()) {
            do {
                String string = c.getString(0).toLowerCase();
                if (string.contains(query)) {
                    c.close();
                    
                    return true;
                }
            } while (c.moveToNext());
        }
        c.close();
        
        return false;
    }

    public String isInstitutionAlreadyEntered(String query) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = new String[]{InstitutionTable.INSTITUTION};
        Cursor c = db.query(InstitutionTable.TABLE_NAME, cols, null, null, null, null, null);
        query = query.toLowerCase();
        if (c.moveToFirst()) {
            do {
                String string = c.getString(0).toLowerCase();
                if (string.contains(query)) {
                    String result = c.getString(0);
                    c.close();
                    
                    return result;
                }
            } while (c.moveToNext());
        }
        c.close();
        
        return null;
    }

    public Bundle[] getNewStories(int feedNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{StoryTable.ID, StoryTable.DATE};
        Cursor c = db.query(StoryTable.TABLE_NAME, col, null, null, null, null, null);
        HashMap<java.sql.Timestamp, Integer> timestampMap = new HashMap<>();
        ArrayList<java.sql.Timestamp> timestampList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(c.getString(1));
                    timestampMap.put(timestamp, c.getInt(0));
                    timestampList.add(timestamp);
                } while (c.moveToNext());
            }
            c.close();
            
        }
        Collections.sort(timestampList);
        int size;
        System.out.println(timestampList.size());
        if (timestampList.size() < feedNumber) {
            size = timestampList.size();
        } else {
            size = feedNumber;
        }
        Bundle[] result = new Bundle[size];
        for (int i = 0; i < size; i++) {
            result[i] = getStory(StoryTable.ID, String.valueOf(timestampMap.get(timestampList.get(timestampList.size() - 1 - i))));
        }
        System.out.println("Story: returns Bundle[]");
        return result;
    }

    public Bundle[] getNewWikis(int feedNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{WikiTable.ID, WikiTable.CREATED};
        Cursor c = db.query(WikiTable.TABLE_NAME, col, null, null, null, null, null);
        HashMap<java.sql.Timestamp, Integer> timestampMap = new HashMap<>();
        ArrayList<java.sql.Timestamp> timestampList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(c.getString(1));
                    timestampMap.put(timestamp, c.getInt(0));
                    timestampList.add(timestamp);
                } while (c.moveToNext());
            }
            c.close();
            
        }
        Collections.sort(timestampList);
        int size;
        if (timestampList.size() < feedNumber) {
            size = timestampList.size();
        } else {
            size = feedNumber;
        }
        Bundle[] result = new Bundle[size];
        for (int i = 0; i < size; i++) {
            result[i] = getWiki(WikiTable.ID, String.valueOf(timestampMap.get(timestampList.get(timestampList.size() - 1 - i))));
        }
        System.out.println("Wiki: returns Bundle[]");
        return result;
    }

    public Bundle[] getPopularPosts(int feedNumber) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{PostTable.ID, PostTable.UP_VOTES, PostTable.DOWN_VOTES};
        Cursor c = db.query(PostTable.TABLE_NAME, col, null, null, null, null, null);
        HashMap<Double, Integer> feedbackMap = new HashMap<>();
        ArrayList<Double> feedbackList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    double total = c.getInt(1) + c.getInt(2);
                    do {
                        total += .001;
                    } while (feedbackList.contains(total));
                    feedbackList.add(total);
                    feedbackMap.put(total, c.getInt(0));
                } while (c.moveToNext());
            }
            c.close();
            
        }
        Collections.sort(feedbackList);
        int size;
        if (feedbackList.size() < feedNumber) {
            size = feedbackList.size();
        } else {
            size = feedNumber;
        }
        Bundle[] result = new Bundle[size];
        for (int i = 0; i < size; i++) {
            Integer id = feedbackMap.get(feedbackList.get(feedbackList.size() - 1 - i));
            result[i] = getPost(PostTable.ID, String.valueOf(id));
        }
        System.out.println("Post: returns Bundle[]");
        return result;
    }

    public Bundle[] getPopularInstitutions(int feedNumber) {
        Timestamp timeStarted = new Timestamp(new Date().getTime());
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{InstitutionTable.ID, InstitutionTable.POSITIVE, InstitutionTable.NEGATIVE};
        Cursor c = db.query(InstitutionTable.TABLE_NAME, col, null, null, null, null, null);
        HashMap<Double, Integer> feedbackMap = new HashMap<>();
        ArrayList<Double> feedbackList = new ArrayList<>();
        if (c != null && c.getCount() > 0) {
            if (c.moveToFirst()) {
                do {
                    double total = c.getInt(1) + c.getInt(2);
                    do {
                        total += .001;
                    } while (feedbackList.contains(total));
                    feedbackList.add(total);
                    feedbackMap.put(total, c.getInt(0));
                } while (c.moveToNext());
            }
            System.out.println(c.getCount());
            c.close();
            
        }
        Collections.sort(feedbackList);
        int size;
        if (feedbackList.size() < feedNumber) {
            size = feedbackList.size();
        } else {
            size = feedNumber;
        }
        Bundle[] result = new Bundle[size];
        for (int i = 0; i < size; i++) {
            Integer id = feedbackMap.get(feedbackList.get(feedbackList.size() - 1 - i));
            result[i] = getInstitution(InstitutionTable.ID, String.valueOf(id));
            System.out.println("Total: " + (feedbackList.size() - 1 - i));
            System.out.println("ID: " + feedbackMap.get(feedbackList.get(feedbackList.size() - 1 - i)));
        }
        System.out.println("Institution: returns Bundle[]");
        System.out.println("Finding most popular institutions started " + um.getTimeFromNow(String.valueOf(timeStarted)));
        return result;
    }

    public Cursor getColumnByUsername(String column, String username) {
        SQLiteDatabase db = getWritableDatabase();
        String[] col = new String[]{column};
        String[] selectionArgs = new String[]{username};
        return db.query(UserTable.TABLE_NAME, col, UserTable.USERNAME + "=?", selectionArgs, null, null, null);
    }

    public Bundle[] getFollowingBundleArray(String dataTable, String followingColumn, String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = getColumnByUsername(followingColumn, username);
        String followingString = null;
        if (c.moveToFirst()) {
            followingString = c.getString(0);
        }
        c.close();
        if (followingString != null && !followingString.contains("null")) {
            String[] splitString = followingString.split(";");
            Bundle[] result = new Bundle[splitString.length];
            for (int i = 0; i < splitString.length; i++) {
                switch (dataTable) {
                    case InstitutionTable.TABLE_NAME:
                        result[i] = getInstitution(InstitutionTable.ID, splitString[i]);
                        break;
                    case WikiTable.TABLE_NAME:
                        result[i] = getWiki(WikiTable.ID, splitString[i]);
                        break;
                    case PostTable.TABLE_NAME:
                        result[i] = getPost(PostTable.ID, splitString[i]);
                        break;
                    default:
                        break;
                }
            }
            return result;
        }
        
        return null;
    }

    public boolean isUserFollowing(String column, String username, Integer id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{column};
        String[] whereArgs = new String[]{username};
        Cursor c = db.query(UserTable.TABLE_NAME, col, UserTable.USERNAME + "=?", whereArgs, null, null, null);
        if (c.moveToFirst()) {
            if (c.getString(0) != null) {
                String[] ids = c.getString(0).split(";");
                c.close();
                
                for (String id1 : ids) {
                    if (id1.equals(String.valueOf(id))) {
                        System.out.println("ID is followed");
                        return true;
                    }
                }
            }
            System.out.println("ID is not followed");
            return false;
        }
        return false;
    }

    public long updateUserFollowing(String column, String username, Integer id) {
        SQLiteDatabase db = getReadableDatabase();
        String[] col = new String[]{column};
        String[] whereArgs = new String[]{username};
        Cursor c = db.query(UserTable.TABLE_NAME, col, UserTable.USERNAME + "=?", whereArgs, null, null, null);
        if (c.moveToFirst()) {
            long result;
            ContentValues cv = new ContentValues();
            String followingIds = c.getString(0);
            String stringId = String.valueOf(id);
            c.close();
            
            if (followingIds == null) {
                followingIds = "";
            }
            if (followingIds.contains("null")) {
                followingIds = "";
            }
            if (!followingIds.contains(stringId)) {
                SQLiteDatabase wd = getWritableDatabase();
                followingIds += id + ";";
                cv.put(column, followingIds);
                result = wd.update(UserTable.TABLE_NAME, cv, UserTable.USERNAME + "=?", whereArgs);
                
                System.out.println("ID: " + id + " was added to following. Now following: " + followingIds);
                return result;
            } else {
                SQLiteDatabase wd = getWritableDatabase();
                String[] idsArray = followingIds.split(";");
                ArrayList<String> idsArrayList = new ArrayList<>();
                Collections.addAll(idsArrayList, idsArray);
                idsArrayList.remove(stringId);
                followingIds = "";
                for (int i = 0; i < idsArrayList.size(); i++) {
                    followingIds += idsArrayList.get(i) + ";";
                }
                cv.put(column, followingIds);
                result = wd.update(UserTable.TABLE_NAME, cv, UserTable.USERNAME + "=?", whereArgs);
                
                System.out.println("ID: " + id + " was removed from following. Now just following: " + followingIds);
                return result;
            }
        }
        return -1;
    }

    public long updatePostRatingAndUserHistory(Integer id, String type, String username) {
        String prevChecked = checkUserPostRated(username, id);
        System.out.println(prevChecked);
        if (prevChecked != null) {
            if (prevChecked.equals(PostTable.UP_VOTES) && type.equals(PostTable.UP_VOTES)) {
                String[] cols = new String[]{PostTable.UP_VOTES};
                String[] colsUser = new String[]{UserTable.POSTS_POSITIVE};
                Integer[] values = new Integer[]{-1};
                updatePostRating(id, cols, values);
                return updateUserPostRatingHistory(id, colsUser, username);
            } else if (prevChecked.equals(PostTable.UP_VOTES) && type.equals(PostTable.DOWN_VOTES)) {
                HashMap<String, Integer> map = new HashMap<>();
                String[] cols = new String[]{PostTable.UP_VOTES, PostTable.DOWN_VOTES};
                String[] colsUser = new String[]{UserTable.POSTS_POSITIVE, UserTable.POSTS_NEGATIVE};
                Integer[] values = new Integer[]{-1, 1};
                updatePostRating(id, cols, values);
                return updateUserPostRatingHistory(id, colsUser, username);
            } else if (prevChecked.equals(PostTable.DOWN_VOTES) && type.equals(PostTable.UP_VOTES)) {
                String[] cols = new String[]{PostTable.UP_VOTES, PostTable.DOWN_VOTES};
                String[] colsUser = new String[]{UserTable.POSTS_POSITIVE, UserTable.POSTS_NEGATIVE};
                Integer[] values = new Integer[]{1, -1};
                updatePostRating(id, cols, values);
                return updateUserPostRatingHistory(id, colsUser, username);
            } else if (prevChecked.equals(PostTable.DOWN_VOTES) && type.equals(PostTable.DOWN_VOTES)) {
                String[] cols = new String[]{PostTable.DOWN_VOTES};
                String[] colsUser = new String[]{UserTable.POSTS_NEGATIVE};
                Integer[] values = new Integer[]{-1};
                updatePostRating(id, cols, values);
                return updateUserPostRatingHistory(id, colsUser, username);
            }
        } else if (type.equals(PostTable.UP_VOTES)) {
            String[] cols = new String[]{PostTable.UP_VOTES};
            String[] colsUser = new String[]{UserTable.POSTS_POSITIVE};
            Integer[] values = new Integer[]{1};
            updatePostRating(id, cols, values);
            return updateUserPostRatingHistory(id, colsUser, username);
        } else if (type.equals(PostTable.DOWN_VOTES)) {
            String[] cols = new String[]{PostTable.DOWN_VOTES};
            String[] colsUser = new String[]{UserTable.POSTS_NEGATIVE};
            Integer[] values = new Integer[]{1};
            updatePostRating(id, cols, values);
            return updateUserPostRatingHistory(id, colsUser, username);
        }
        return -1;
    }

    public String checkUserPostRated(String username, Integer id) {
        Bundle bundle = getUser(UserTable.USERNAME, username);
        String posString = bundle.getString("POSTS_POSITIVE");
        if (posString != null) {
            if (!posString.contains("null")) {
                String[] positives = posString.split(";");
                ArrayList<String> positivesArrayList = new ArrayList<>();
                Collections.addAll(positivesArrayList, positives);
                if (positivesArrayList.contains(String.valueOf(id)))
                    return PostTable.UP_VOTES;
            }
        } else {
            String negString = bundle.getString("POSTS_NEGATIVE");
            if (negString != null) {
                if (!negString.contains("null")) {
                    String[] negatives = negString.split(";");
                    ArrayList<String> negativesArrayList = new ArrayList<>();
                    Collections.addAll(negativesArrayList, negatives);
                    if (negativesArrayList.contains(String.valueOf(id)))
                        return PostTable.DOWN_VOTES;
                }
            }
        }
        return null;
    }

    public long updatePostRating(Integer id, String[] cols, Integer[] values) {
        SQLiteDatabase wd = getWritableDatabase();
        String[] whereArgs = new String[]{String.valueOf(id)};
        Cursor c = wd.query(PostTable.TABLE_NAME, cols, PostTable.ID + "=?", whereArgs, null, null, null);
        ContentValues cv = new ContentValues();
        if (c.moveToFirst())
            for (int i = 0; i < cols.length; i++) {
                cv.put(cols[i], String.valueOf(Integer.valueOf(c.getString(i)) + values[i]));
            }
        c.close();
        long result = wd.update(PostTable.TABLE_NAME, cv, PostTable.ID + "=?", whereArgs);
        
        return result;
    }

    public long updateUserPostRatingHistory(Integer id, String[] cols, String username) {
        SQLiteDatabase wd = getWritableDatabase();
        String[] whereArgs = new String[]{username};
        Cursor c = wd.query(UserTable.TABLE_NAME, cols, UserTable.USERNAME + "=?", whereArgs, null, null, null);
        ContentValues cv = new ContentValues();
        if (c.moveToFirst()) {
            for (int i = 0; i < cols.length; i++) {
                if (c.getString(i) != null) {
                    if (!c.getString(i).contains("null")) {
                        String[] idsArray = c.getString(i).split(";");
                        ArrayList<String> idsArrayList = new ArrayList<>();
                        String newContentString = null;
                        Collections.addAll(idsArrayList, idsArray);
                        if (idsArrayList.contains(String.valueOf(id))) {
                            idsArrayList.remove(String.valueOf(id));
                        } else {
                            idsArrayList.add(String.valueOf(id));
                        }
                        for (int m = 0; m < idsArrayList.size(); m++) {
                            newContentString += idsArrayList.get(m) + ";";
                        }
                        cv.put(cols[i], newContentString);
                    } else {
                        cv.put(cols[i], String.valueOf(id) + ";");
                    }
                } else {
                    cv.put(cols[i], String.valueOf(id) + ";");
                }

            }
            c.close();
        }
        long result = wd.update(UserTable.TABLE_NAME, cv, UserTable.USERNAME + "=?", whereArgs);
        
        return result;
    }

    public long updateViews(Integer id, String table, String table_id, String col) {
        SQLiteDatabase db = getWritableDatabase();
        String[] cols = new String[]{col};
        String[] whereArgs = new String[]{String.valueOf(id)};
        Cursor c = db.query(table, cols, table_id + "=?", whereArgs, null, null, null, null);
        if (c.moveToFirst()) {
            ContentValues cv = new ContentValues();
            Integer views;
            if (c.getString(0) == null)
                views = 0;
            else
                views = Integer.valueOf(c.getString(0));
            views++;
            cv.put("VIEWS", String.valueOf(views));
            long result = db.update(table, cv, table_id + "=?", whereArgs);
            c.close();
            
            System.out.println("View Count++");
            return result;
        }
        return -1;
    }
}
