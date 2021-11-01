package xyz.muntasiraonik.oneaccountperdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AbsRuntimePermission {
    FirebaseAuth firebaseAuth;
    private static final int REQUEST_PERMISSION = 10;
    TelephonyManager telephonyManager;
    private String android_id;
    private String Uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        requestAppPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE},

                R.string.msg, REQUEST_PERMISSION);
        Button Lout = findViewById(R.id.logout);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Uid = firebaseUser.getUid();
        firebaseAuth = FirebaseAuth.getInstance();
        Lout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    @SuppressLint("HardwareIds")
    @Override
    public void onPermissionsGranted(int requestCode) {
        telephonyManager = (TelephonyManager) getSystemService(Context.
                TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android_id =  Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }else {
            android_id = telephonyManager.getDeviceId();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference NRef = FirebaseDatabase.getInstance().getReference();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             // if you want to check if the Currrent user is same one, who created the account with this device.
             // get the email under UserId Child
             // get the email under UID
             // now compare the both email
             // if the Current user email is not the same to the email that is set to the device
             // show alert



            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        NRef.addValueEventListener(eventListener);

    }
}
