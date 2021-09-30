package com.urunggundrup.gotani;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences pref;
    int PRIVATE_MODE = 0;
    Context _context;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "GoTani";
    private static final String IS_LOGIN = "IsLoggedIn";
    // User name (make variable public to access from outside)
    public static final String USER_ID = "iduser";
    public static final String USER_USERNAME = "username";
    public static final String USER_PASSWORD = "password";
    public static final String USER_NAMA = "nama";
    public static final String USER_NOHP = "nohp";
    public static final String USER_LOKASI = "lokasi";
    public static final String USER_STATUS = "status";
    public static final String USER_ID_TOKO = "idtoko";
    public static final String USER_NAMA_TOKO = "namatoko";
    public static final String USER_CREATED_DATE = "createddate";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String id, String username, String password, String nama, String nohp, String lokasi, String status, String idtoko, String namatoko, String createddate){
// Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
// Storing name in pref
        editor.putString(USER_ID, id);
        editor.putString(USER_USERNAME, username);
        editor.putString(USER_PASSWORD, password);
        editor.putString(USER_NAMA, nama);
        editor.putString(USER_NOHP, nohp);
        editor.putString(USER_LOKASI, lokasi);
        editor.putString(USER_STATUS, status);
        editor.putString(USER_ID_TOKO, idtoko);
        editor.putString(USER_NAMA_TOKO, namatoko);
        editor.putString(USER_CREATED_DATE, createddate);

        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(USER_USERNAME, pref.getString(USER_USERNAME, null));
        user.put(USER_PASSWORD, pref.getString(USER_PASSWORD, null));
        user.put(USER_NAMA, pref.getString(USER_NAMA, null));
        user.put(USER_NOHP, pref.getString(USER_NOHP, null));
        user.put(USER_LOKASI, pref.getString(USER_LOKASI, null));
        user.put(USER_STATUS, pref.getString(USER_STATUS, null));
        user.put(USER_ID_TOKO, pref.getString(USER_ID_TOKO, null));
        user.put(USER_NAMA_TOKO, pref.getString(USER_NAMA_TOKO, null));
        user.put(USER_CREATED_DATE, pref.getString(USER_CREATED_DATE, null));
        return user;
    }

    public boolean checkLogin(){
        if(!this.isLoggedIn())
            return false;
        else
            return true;
    }


    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, MainActivity.class);
        i.putExtra("status","1");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
