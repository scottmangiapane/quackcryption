package co.twoduck.quackcryption;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private AppCompatActivity activity;
    private Preferences preferences;
    private TextView initVector;
    private TextView key;
    private Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        activity = this;
        preferences = new Preferences(this);
        initVector = (TextView) findViewById(R.id.input_init_vector);
        key = (TextView) findViewById(R.id.input_key);
        initVector.setText(preferences.getInitVector());
        key.setText(preferences.getKey());
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.transition.slide_right_1, R.transition.slide_right_2);
            }
        });
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initVector.setText(preferences.genRand());
                key.setText(preferences.genRand());
            }
        });
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save()) {
                    if (t != null)
                        t.cancel();
                    t = Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        if (preferences.showWarning()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning")
                    .setMessage("If you change the key or init vector, any previously encrypted text will be impossible to decrypt.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .setNegativeButton("Don't show again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            preferences.hideWarning();
                        }
                    })
                    .create().show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.slide_right_1, R.transition.slide_right_2);
    }

    private boolean save() {
        if (!String.valueOf(initVector.getText()).equals(""))
            if (initVector.getText().length() % 16 != 0) {
                if (t != null)
                    t.cancel();
                t = Toast.makeText(this, "Init vector must be divisible by 16", Toast.LENGTH_SHORT);
                t.show();
                return false;
            } else
                preferences.setInitVector(String.valueOf(initVector.getText()));
        if (!String.valueOf(key.getText()).equals(""))
            if (key.getText().length() % 16 != 0) {
                if (t != null)
                    t.cancel();
                t = Toast.makeText(this, "Key must be divisible by 16", Toast.LENGTH_SHORT);
                t.show();
                return false;
            } else
                preferences.setKey(String.valueOf(key.getText()));
        return true;
    }
}
