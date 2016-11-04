package co.twoduck.quackcryption;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Preferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Preferences(Activity activity) {
        sharedPreferences = activity.getSharedPreferences("twoduck", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getKey() {
        String s = sharedPreferences.getString("key", "");
        if (s.equals("")) {
            s = genRand();
            setKey(s);
        }
        return s;
    }

    public void setKey(String data) {
        editor.putString("key", data);
        editor.commit();
    }

    public String getInitVector() {
        String s = sharedPreferences.getString("init_vector", "");
        if (s.equals("")) {
            s = genRand();
            setInitVector(s);
        }
        return s;
    }

    public void setInitVector(String data) {
        editor.putString("init_vector", data);
        editor.commit();
    }

    public String genRand() {
        String s = "";
        for (int i = 0; i < 16; i++)
            s += (char) (Math.random() * 26 + 'A');
        Log.w("############", s);
        return s;
    }

    public boolean showWarning() {
        return sharedPreferences.getBoolean("show_warning", true);
    }

    public void hideWarning() {
        editor.putBoolean("show_warning", false);
        editor.commit();
    }
}
