package co.twoduck.quackcryption;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Preferences(Activity activity) {
        sharedPreferences = activity.getSharedPreferences("twoduck", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getKey() {
        return sharedPreferences.getString("key", "");
    }

    public void setKey(String data) {
        editor.putString("key", data);
        editor.commit();
    }

    public String getInitVector() {
        return sharedPreferences.getString("init_vector", "");
    }

    public void setInitVector(String data) {
        editor.putString("init_vector", data);
        editor.commit();
    }
}
