package dk.easv.friendsv2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Debug;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dk.easv.friendsv2.Model.BEFriend;

public class DetailActivity extends AppCompatActivity {

    static int PERMISSION_TO_SMS_CODE = 1;
    static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE = 101;
    static int PERMISSION_REQUEST_CODE = 2;
    String TAG = MainActivity.TAG;
    EditText etName;
    EditText etPhone;
    CheckBox cbFavorite;
    BEFriend friend;
    ImageView image;

    File mFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.d(TAG, "Detail Activity started");
        checkPermissions();

        image = findViewById(R.id.iv_image);
        image.setImageResource(R.drawable.qmark);
        Button photoButton = findViewById(R.id.btnPhoto);
        photoButton.setOnClickListener(new View.OnClickListener() {
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

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancel();
            }
        });

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        cbFavorite = findViewById(R.id.cbFavorite);

        setGUI();


    }

    private void checkPermissions() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) return;

        ArrayList<String> permissions = new ArrayList<String>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA);
        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        //    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissions.size() > 0) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), PERMISSION_REQUEST_CODE);
        }
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE_BY_FILE) {
            if (resultCode == RESULT_OK) {
                image.setImageURI(Uri.fromFile(mFile));
                //mImage.setBackgroundColor(Color.RED);
                //mImage.setRotation(90);
            } else handleOther(resultCode);
        }
    }

    private void handleOther(int resultCode) {
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Canceled...", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Picture NOT taken - unknown error...", Toast.LENGTH_LONG).show();
        }
    }

    private void startSMSActivity() {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        String phoneNumber = etPhone.getText().toString();
        sendIntent.setData(Uri.parse("sms:" + phoneNumber));
        sendIntent.putExtra("sms_body", "IT WOOOORKS");
        startActivity(sendIntent);
    }

    public void makeCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String phoneNumber = etPhone.getText().toString();
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void setGUI() {
        BEFriend f = (BEFriend) getIntent().getSerializableExtra("friend");

        etName.setText(f.getName());
        etPhone.setText(f.getPhone());
        cbFavorite.setChecked(f.isFavorite());
        if (!f.getPhotoUrl().isEmpty()){
            image.setImageURI(Uri.parse(f.getPhotoUrl()));
        } else {
            image.setImageResource(R.drawable.qmark);
        }
    }

    protected void onClickOK() {
        Intent data = new Intent();
        friend = new BEFriend(String.valueOf(etName.getText()),
                etPhone.getText().toString(), cbFavorite.isChecked(), mFile.getAbsolutePath());
        data.putExtra("updatedFriend", friend);
        setResult(RESULT_OK, data);
        Log.d("Cake", "onClickOK: filepath = " + mFile.getAbsolutePath());
        finish();
    }

    protected void onClickCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
