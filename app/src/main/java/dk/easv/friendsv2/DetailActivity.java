package dk.easv.friendsv2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dk.easv.friendsv2.Model.BEFriend;

public class DetailActivity extends AppCompatActivity {

    static int PERMISSION_TO_SMS_CODE = 1;
    static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE = 101;
    static int PERMISSION_REQUEST_CODE = 2;

    // TAG used for logging
    String TAG = MainActivity.TAG;

    // Edit text fields for name and phone number
    EditText etName, etPhone;

    // Check box for if the friend is a favorite
    CheckBox cbFavorite;

    // The current friend
    BEFriend friend;

    // The image view holding the current friends image
    ImageView image;

    // The picture taken with the camera app
    File mFile;

    LocationManager locationManager;

    // The home address in latitude and longitude
    Double homeLatitude, homeLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.d(TAG, "Detail Activity started");
        checkPermissions();

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        cbFavorite = findViewById(R.id.cbFavorite);

        image = findViewById(R.id.iv_image);
        image.setImageResource(R.drawable.qmark);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraWithFileProvider();
            }
        });
        Button callButton = findViewById(R.id.btnCall);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall();
            }
        });
        Button smsButton = findViewById(R.id.btnSms);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showYesNoDialog();
            }
        });
        Button okButton = findViewById(R.id.btnOk);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOK();
            }
        });
        Button cancelButton = findViewById(R.id.btnCancel);
        Button browserBtn = findViewById(R.id.btnBrowser);
        setGUI();
        browserBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                startBrowser();

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancel();
            }
        });

        Button homeBtn = findViewById(R.id.btnHome);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHomeLocation();
            }
        });

        Button showLocBtn = findViewById(R.id.showLoc);
        showLocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Creates an Intent that will load a map with a friends home location
                double friendHomeLat = friend.getHomeLatitude();
                double friendHomeLon = friend.getHomeLongitude();
                Intent mapIntent = new Intent(DetailActivity.this, MapsActivity.class);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.putExtra("FriendLat", friendHomeLat);
                mapIntent.putExtra("FriendLong", friendHomeLon);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        setGUI();
        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);




    }

    // Sets the home location for the current friend
    // and also saves the doubles in the instance variables
    private void setHomeLocation() {

        Location loc = lastKnownLocation();
        if (loc == null) {
            Toast.makeText(getApplicationContext(), "Last known location is null",
                    Toast.LENGTH_LONG).show();
            return;
        }
        homeLatitude = loc.getLatitude();
        homeLongitude = loc.getLongitude();
        if (friend != null) {
            // Change to instance variable maybe
            friend.setHomeLatitude(homeLatitude);
            friend.setHomeLongitude(homeLongitude);
        }


        Toast.makeText(getApplicationContext(), "Home Location: " + homeLatitude + homeLongitude,
                Toast.LENGTH_SHORT).show();
    }

    // Checks for permissions
    // and either gets the location of the device or returns null
    private Location lastKnownLocation() {
        boolean GPSPermissionGiven = true;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            GPSPermissionGiven = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return GPSPermissionGiven ? locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER) : null;
    }

    // Handles permissions
    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) return;

        ArrayList<String> permissions = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
        }
    }

    // Handles camera through file provider
    private void openCameraWithFileProvider() {
        mFile = getOutputMediaFile();
        if (mFile == null) {
            Toast.makeText(this, "Could not create file...", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                this,
                "dk.easv.friendsv2.provider", mFile
        ));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE);
        } else {
            Log.d(TAG, "camera app could NOT be started");
        }
    }

    // Creates and returns the media file when a picture is taken
    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Camera01");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("xyz", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String postfix = "jpg";
        String prefix = "IMG";

        File mediaFile = new File(mediaStorageDir.getPath() +
                File.separator + prefix +
                "_" + timeStamp + "." + postfix);

        return mediaFile;
    }

    // starts a dialog for SMS
    private void showYesNoDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("SMS Handling")
                .setMessage("Click Direct if SMS should be send directly. Click Start to start SMS app...")
                .setCancelable(true)
                .setPositiveButton("Direct", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DetailActivity.this.sendSMS();
                    }
                })
                .setNegativeButton("Start", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DetailActivity.this.startSMSActivity();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    // Sends SMS if permission has been given and stops the request if not.
    private void sendSMS() {
        Toast.makeText(this, "An sms will be send", Toast.LENGTH_LONG)
                .show();


        Log.d(TAG, "Build version = " + android.os.Build.VERSION.SDK_INT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d(TAG, "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_TO_SMS_CODE);
                return;

            } else
                Log.d(TAG, "permission to SEND_SMS granted!");

        }
        String phoneNumber = etPhone.getText().toString();
        SmsManager m = SmsManager.getDefault();
        String text = "Hi, it goes well on the android course...";
        m.sendTextMessage(phoneNumber, null, text, null, null);
    }

    // Gets the result from the text message.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_TO_SMS_CODE) {
            Log.d(TAG, "Permission: " + permissions[0] + " - grantResult: " + grantResults[0]);

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager m = SmsManager.getDefault();
                String phoneNumber = etPhone.getText().toString();
                String text = "Hi, it goes well on the android course...";
                m.sendTextMessage(phoneNumber, null, text, null, null);
            }
        } else
            Log.d(TAG, "Unknown permission request code: " + requestCode);
    }

    // Sets the picture received from the camera intent on the ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE) {
            if (resultCode == RESULT_OK) {
                image.setImageURI(Uri.fromFile(mFile));
            } else handleOther(resultCode);
        }
    }

    // handles errors or if the request has been cancelled.
    private void handleOther(int resultCode) {
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }

    // Handles starting the sms activity
    private void startSMSActivity() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        String phoneNumber = etPhone.getText().toString();
        sendIntent.setData(Uri.parse("sms:" + phoneNumber));
        sendIntent.putExtra("sms_body", "IT WOOOORKS");
        startActivity(sendIntent);
    }

    // Handles starting a call activity
    public void makeCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String phoneNumber = etPhone.getText().toString();
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    // Handles starting a browser activity
    private void startBrowser() {
        String url = "http://www.dr.dk";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    // Sets the GUI based on the friend received from the main activity
    private void setGUI() {
        friend = (BEFriend) getIntent().getSerializableExtra("friend");
        if (friend != null) {
            Log.d(TAG, "setGUI: name" + friend.getName());
            etName.setText(friend.getName());
            etPhone.setText(friend.getPhone());
            cbFavorite.setChecked(friend.isFavorite());
            if (!friend.getPhotoUrl().isEmpty()) {
                image.setImageURI(Uri.parse(friend.getPhotoUrl()));
            } else {
                image.setImageResource(R.drawable.qmark);
            }
        }
    }

    // Updates or creates a friend depending on how the activity was opening
    protected void onClickOK() {
        Intent data = new Intent();
        BEFriend f;
        String filepath = getFilePath();


        if (friend != null) {

            f = new BEFriend(friend.getId(), String.valueOf(etName.getText()),
                    etPhone.getText().toString(), cbFavorite.isChecked(), filepath);
            if (homeLongitude != null && homeLatitude != null) {
                f.setHomeLatitude(homeLatitude);
                f.setHomeLongitude(homeLongitude);
            }
            data.putExtra("updatedFriend", f);

            Log.d(TAG, "onClickOK updated: Friend = " + f);
            setResult(RESULT_OK, data);
        } else {
            f = new BEFriend(0, String.valueOf(etName.getText()),
                    etPhone.getText().toString(), cbFavorite.isChecked(), filepath);
            if (homeLongitude != null && homeLatitude != null) {
                f.setHomeLatitude(homeLatitude);
                f.setHomeLongitude(homeLongitude);
            }
            data.putExtra("newFriend", f);
            Log.d(TAG, "onClickOK: new: Friend = " + f);
            setResult(RESULT_FIRST_USER, data);
        }

        finish();
    }

    // Returns the filepath as a string
    private String getFilePath() {
        String filepath;
        if (mFile != null) {
            filepath = mFile.getAbsolutePath();
        } else if (friend != null && !friend.getPhotoUrl().isEmpty()) {
            filepath = friend.getPhotoUrl();
        } else {
            Uri path = Uri.parse("android.resource://dk.easv.friendsv2/" + R.drawable.qmark);
            filepath = path.toString();
        }
        return filepath;
    }

    // Finishes the intent and sets result as canceled
    protected void onClickCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
