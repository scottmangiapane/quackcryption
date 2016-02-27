package co.twoduck.quackcryption;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private FileUriCallback mFileUriCallback;
    private NfcAdapter mNfcAdapter;
    private boolean isUnlocked;
    private EditText textBox;
    private ImageView duckIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mFileUriCallback = new FileUriCallback();
        mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        isUnlocked = true;
        textBox = (EditText) findViewById(R.id.text_box);
        duckIcon = (ImageView) findViewById(R.id.duck);
        duckIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUnlocked) {
                    duckIcon.setImageResource(R.drawable.duck_locked);
                    textBox.setText(quackCryptor(encrypt("QuackQuackQuack!", "RandomInitVector",
                            String.valueOf(textBox.getText()))));
                    textBox.setFocusable(false);
                    textBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("simple text", "Hello, World!");
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getBaseContext(), "Copied!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    duckIcon.setImageResource(R.drawable.duck_unlocked);
                    textBox.setText(quackReader(decrypt("QuackQuackQuack!", "RandomInitVector",
                            String.valueOf(textBox.getText()))));
                    textBox.setFocusableInTouchMode(true);
                    textBox.setOnClickListener(null);
                }
                isUnlocked = !isUnlocked;
                // play sound
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.duck);
                mp.start();
            }
        });
    }

    // A File object containing the path to the transferred files
    private File mParentPath;
    // Incoming Intent
    private Intent mIntent;

    /*
     * Called from onNewIntent() for a SINGLE_TOP Activity
     * or onCreate() for a new Activity. For onNewIntent(),
     * remember to call setIntent() to store the most
     * current Intent
     *
     */
    private void handleViewIntent() {
        // Get the Intent action
        mIntent = getIntent();
        String action = mIntent.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            Uri beamUri = mIntent.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mParentPath = new File(handleFileUri(beamUri));
            } else if (TextUtils.equals(
                    beamUri.getScheme(), "content")) {
                mParentPath = new File(handleContentUri(beamUri));
            }
        }
    }

    public String handleFileUri(Uri beamUri) {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory
        return copiedFile.getParent();
    }

    private Uri[] mFileUris = new Uri[10];

    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {
        }

        /**
         * Create content URIs as needed to share with another device
         */
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }

    public String handleContentUri(Uri beamUri) {
        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(), MediaStore.AUTHORITY)) {
            /*
             * Handle content URIs for other content providers
             */
            // For a MediaStore content URI
        } else {
            // Get the column that contains the file name
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor pathCursor =
                    getContentResolver().query(beamUri, projection,
                            null, null, null);
            // Check for a valid cursor
            if (pathCursor != null &&
                    pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(
                        MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                // Return the parent directory of the file
                return copiedFile.getParent();
            } else {
                // The query didn't work; return null
                return null;
            }
        }
        return null;
    }

    private void updateText() {
        /*
         * Create a list of URIs, get a File,
         * and set its permissions
         */
        Uri[] mFileUris = new Uri[10];
        String transferFile = "text.txt";
        File extDir = getExternalFilesDir(null);
        File requestFile = new File(extDir, transferFile);
        requestFile.setReadable(true, false);
        // Get a URI for the File and add it to the list of URIs
        Uri fileUri = Uri.fromFile(requestFile);
        if (fileUri != null) {
            mFileUris[0] = fileUri;
        } else {
            Log.e("My Activity", "No File URI available for file.");
        }
    }

    /**
     * The quackCryptor method takes one parameter, a string of ASCII characters, and
     * converts each character into a variant of two quacks with different capitalization.
     * It returns the "quacked" text.
     *
     * @param str1 the string to be encrypted
     * @return quacked an encrypted string
     */

    private String quackCryptor(String str1) {
        String quacked = "";
        char[] inputArray = str1.toCharArray();
        for (int i = 0; i < inputArray.length; i++) {
            switch (inputArray[i]) {
                case 43:
                    quacked = quacked + "qUACKQUAcK";
                    break;
                case 47:
                    quacked = quacked + "quackquack";
                    break;
                case 48:
                    quacked = quacked + "Quackquack";
                    break;
                case 49:
                    quacked = quacked + "qUackquack";
                    break;
                case 50:
                    quacked = quacked + "quAckquack";
                    break;
                case 51:
                    quacked = quacked + "quaCkquack";
                    break;
                case 52:
                    quacked = quacked + "quacKquack";
                    break;
                case 53:
                    quacked = quacked + "quackQuack";
                    break;
                case 54:
                    quacked = quacked + "quackqUack";
                    break;
                case 55:
                    quacked = quacked + "quackquAck";
                    break;
                case 56:
                    quacked = quacked + "quackquaCk";
                    break;
                case 57:
                    quacked = quacked + "quackquacK";
                    break;
                case 61:
                    quacked = quacked + "QUackquack";
                    break;
                case 65:
                    quacked = quacked + "QuAckquack";
                    break;
                case 66:
                    quacked = quacked + "QuaCkquack";
                    break;
                case 67:
                    quacked = quacked + "QuacKquack";
                    break;
                case 68:
                    quacked = quacked + "QuackQuack";
                    break;
                case 69:
                    quacked = quacked + "QuackqUack";
                    break;
                case 70:
                    quacked = quacked + "QuackquAck";
                    break;
                case 71:
                    quacked = quacked + "QuackquaCk";
                    break;
                case 72:
                    quacked = quacked + "QuackquacK";
                    break;
                case 73:
                    quacked = quacked + "QUAckquack";
                    break;
                case 74:
                    quacked = quacked + "QUaCkquack";
                    break;
                case 75:
                    quacked = quacked + "QUacKquack";
                    break;
                case 76:
                    quacked = quacked + "QUackQuack";
                    break;
                case 77:
                    quacked = quacked + "QUackqUack";
                    break;
                case 78:
                    quacked = quacked + "QUackquAck";
                    break;
                case 79:
                    quacked = quacked + "QUackquaCk";
                    break;
                case 80:
                    quacked = quacked + "QUackquacK";
                    break;
                case 81:
                    quacked = quacked + "QUACkquack";
                    break;
                case 82:
                    quacked = quacked + "QUAcKquack";
                    break;
                case 83:
                    quacked = quacked + "QUAckQuack";
                    break;
                case 84:
                    quacked = quacked + "QUAckqUack";
                    break;
                case 85:
                    quacked = quacked + "QUAckquAck";
                    break;
                case 86:
                    quacked = quacked + "QUAckquaCk";
                    break;
                case 87:
                    quacked = quacked + "QUAckquacK";
                    break;
                case 88:
                    quacked = quacked + "QUACKquack";
                    break;
                case 89:
                    quacked = quacked + "QUACkQuack";
                    break;
                case 90:
                    quacked = quacked + "QUACkqUack";
                    break;
                case 97:
                    quacked = quacked + "QUACkquAck";
                    break;
                case 98:
                    quacked = quacked + "QUACkquaCk";
                    break;
                case 99:
                    quacked = quacked + "QUACkquacK";
                    break;
                case 100:
                    quacked = quacked + "QUACKQuack";
                    break;
                case 101:
                    quacked = quacked + "QUACKqUack";
                    break;
                case 102:
                    quacked = quacked + "QUACKquAck";
                    break;
                case 103:
                    quacked = quacked + "QUACKquaCk";
                    break;
                case 104:
                    quacked = quacked + "QUACKquacK";
                    break;
                case 105:
                    quacked = quacked + "QUACKQUack";
                    break;
                case 106:
                    quacked = quacked + "QUACKQuAck";
                    break;
                case 107:
                    quacked = quacked + "QUACKQuaCk";
                    break;
                case 108:
                    quacked = quacked + "QUACKQuacK";
                    break;
                case 109:
                    quacked = quacked + "QUACKQUAck";
                    break;
                case 110:
                    quacked = quacked + "QUACKQUaCk";
                    break;
                case 111:
                    quacked = quacked + "QUACKQUacK";
                    break;
                case 112:
                    quacked = quacked + "QUACKQUACk";
                    break;
                case 113:
                    quacked = quacked + "QUACKQUAcK";
                    break;
                case 114:
                    quacked = quacked + "QUACKQUACK";
                    break;
                case 115:
                    quacked = quacked + "qUACKQUACK";
                    break;
                case 116:
                    quacked = quacked + "QuACKQUACK";
                    break;
                case 117:
                    quacked = quacked + "QUaCKQUACK";
                    break;
                case 118:
                    quacked = quacked + "QUAcKQUACK";
                    break;
                case 119:
                    quacked = quacked + "QUACkQUACK";
                    break;
                case 120:
                    quacked = quacked + "QUACKqUACK";
                    break;
                case 121:
                    quacked = quacked + "QUACKQuACK";
                    break;
                case 122:
                    quacked = quacked + "QUACKQUaCK";
                    break;

            }
            quacked = quacked + " ";
        }
        return quacked;
    }

    /**
     * The quackReader method takes one parameter, a quack encrypted string, and
     * converts each series of two quacks with the corresponding character. It returns
     * the translated text.
     *
     * @param str2 the string to be decrypted
     * @return deQuacked the decrypted string
     */

    public String quackReader(String str2) {
        String deQuacked = "";
        String[] translateArray = str2.split(" ");
        for (int j = 0; j < translateArray.length; j++) {
            switch (translateArray[j]) {
                case "qUACKQUAcK":
                    deQuacked = deQuacked + '+';
                    break;
                case "quackquack":
                    deQuacked = deQuacked + '/';
                    break;
                case "Quackquack":
                    deQuacked = deQuacked + '0';
                    break;
                case "qUackquack":
                    deQuacked = deQuacked + '1';
                    break;
                case "quAckquack":
                    deQuacked = deQuacked + '2';
                    break;
                case "quaCkquack":
                    deQuacked = deQuacked + '3';
                    break;
                case "quacKquack":
                    deQuacked = deQuacked + '4';
                    break;
                case "quackQuack":
                    deQuacked = deQuacked + '5';
                    break;
                case "quackqUack":
                    deQuacked = deQuacked + '6';
                    break;
                case "quackquAck":
                    deQuacked = deQuacked + '7';
                    break;
                case "quackquaCk":
                    deQuacked = deQuacked + '8';
                    break;
                case "quackquacK":
                    deQuacked = deQuacked + '9';
                    break;
                case "QUackquack":
                    deQuacked = deQuacked + '=';
                    break;
                case "QuAckquack":
                    deQuacked = deQuacked + 'A';
                    break;
                case "QuaCkquack":
                    deQuacked = deQuacked + 'B';
                    break;
                case "QuacKquack":
                    deQuacked = deQuacked + 'C';
                    break;
                case "QuackQuack":
                    deQuacked = deQuacked + 'D';
                    break;
                case "QuackqUack":
                    deQuacked = deQuacked + 'E';
                    break;
                case "QuackquAck":
                    deQuacked = deQuacked + 'F';
                    break;
                case "QuackquaCk":
                    deQuacked = deQuacked + 'G';
                    break;
                case "QuackquacK":
                    deQuacked = deQuacked + 'H';
                    break;
                case "QUAckquack":
                    deQuacked = deQuacked + 'I';
                    break;
                case "QUaCkquack":
                    deQuacked = deQuacked + 'J';
                    break;
                case "QUacKquack":
                    deQuacked = deQuacked + 'K';
                    break;
                case "QUackQuack":
                    deQuacked = deQuacked + 'L';
                    break;
                case "QUackqUack":
                    deQuacked = deQuacked + 'M';
                    break;
                case "QUackquAck":
                    deQuacked = deQuacked + 'N';
                    break;
                case "QUackquaCk":
                    deQuacked = deQuacked + 'O';
                    break;
                case "QUackquacK":
                    deQuacked = deQuacked + 'P';
                    break;
                case "QUACkquack":
                    deQuacked = deQuacked + 'Q';
                    break;
                case "QUAcKquack":
                    deQuacked = deQuacked + 'R';
                    break;
                case "QUAckQuack":
                    deQuacked = deQuacked + 'S';
                    break;
                case "QUAckqUack":
                    deQuacked = deQuacked + 'T';
                    break;
                case "QUAckquAck":
                    deQuacked = deQuacked + 'U';
                    break;
                case "QUAckquaCk":
                    deQuacked = deQuacked + 'V';
                    break;
                case "QUAckquacK":
                    deQuacked = deQuacked + 'W';
                    break;
                case "QUACKquack":
                    deQuacked = deQuacked + 'X';
                    break;
                case "QUACkQuack":
                    deQuacked = deQuacked + 'Y';
                    break;
                case "QUACkqUack":
                    deQuacked = deQuacked + 'Z';
                    break;
                case "QUACkquAck":
                    deQuacked = deQuacked + 'a';
                    break;
                case "QUACkquaCk":
                    deQuacked = deQuacked + 'b';
                    break;
                case "QUACkquacK":
                    deQuacked = deQuacked + 'c';
                    break;
                case "QUACKQuack":
                    deQuacked = deQuacked + 'd';
                    break;
                case "QUACKqUack":
                    deQuacked = deQuacked + 'e';
                    break;
                case "QUACKquAck":
                    deQuacked = deQuacked + 'f';
                    break;
                case "QUACKquaCk":
                    deQuacked = deQuacked + 'g';
                    break;
                case "QUACKquacK":
                    deQuacked = deQuacked + 'h';
                    break;
                case "QUACKQUack":
                    deQuacked = deQuacked + 'i';
                    break;
                case "QUACKQuAck":
                    deQuacked = deQuacked + 'j';
                    break;
                case "QUACKQuaCk":
                    deQuacked = deQuacked + 'k';
                    break;
                case "QUACKQuacK":
                    deQuacked = deQuacked + 'l';
                    break;
                case "QUACKQUAck":
                    deQuacked = deQuacked + 'm';
                    break;
                case "QUACKQUaCk":
                    deQuacked = deQuacked + 'n';
                    break;
                case "QUACKQUacK":
                    deQuacked = deQuacked + 'o';
                    break;
                case "QUACKQUACk":
                    deQuacked = deQuacked + 'p';
                    break;
                case "QUACKQUAcK":
                    deQuacked = deQuacked + 'q';
                    break;
                case "QUACKQUACK":
                    deQuacked = deQuacked + 'r';
                    break;
                case "qUACKQUACK":
                    deQuacked = deQuacked + 's';
                    break;
                case "QuACKQUACK":
                    deQuacked = deQuacked + 't';
                    break;
                case "QUaCKQUACK":
                    deQuacked = deQuacked + 'u';
                    break;
                case "QUAcKQUACK":
                    deQuacked = deQuacked + 'v';
                    break;
                case "QUACkQUACK":
                    deQuacked = deQuacked + 'w';
                    break;
                case "QUACKqUACK":
                    deQuacked = deQuacked + 'x';
                    break;
                case "QUACKQuACK":
                    deQuacked = deQuacked + 'y';
                    break;
                case "QUACKQUaCK":
                    deQuacked = deQuacked + 'z';
                    break;

            }
        }
        return deQuacked;

    }

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("ASCII"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("ASCII"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ASCII"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
