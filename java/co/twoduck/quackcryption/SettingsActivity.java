package co.twoduck.quackcryption;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private Preferences preferences;
    private TextView initVector;
    private TextView key;
    private Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferences = new Preferences(this);
        initVector = (TextView) findViewById(R.id.input_init_vector);
        key = (TextView) findViewById(R.id.input_key);
        initVector.setText(preferences.getInitVector());
        key.setText(preferences.getKey());
    }

    @Override
    public void onBackPressed() {
        if (!String.valueOf(initVector.getText()).equals(""))
            if (initVector.getText().length() % 16 != 0) {
                t = Toast.makeText(this, "Init vector must be divisible by 16", Toast.LENGTH_SHORT);
                t.show();
                return;
            } else
                preferences.setInitVector(String.valueOf(initVector.getText()));
        if (!String.valueOf(key.getText()).equals(""))
            if (key.getText().length() % 16 != 0) {
                t = Toast.makeText(this, "Key must be divisible by 16", Toast.LENGTH_SHORT);
                t.show();
                return;
            } else
                preferences.setKey(String.valueOf(key.getText()));
        super.onBackPressed();
        overridePendingTransition(R.transition.slide_right_1, R.transition.slide_right_2);
    }
}
