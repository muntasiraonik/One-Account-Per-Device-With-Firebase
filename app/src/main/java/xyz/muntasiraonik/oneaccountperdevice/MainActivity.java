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
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AbsRuntimePermission {
    private Button L,RG;
    private String android_id;
    private EditText IEmail,IPass;
    private static final int REQUEST_PERMISSION = 10;
    TelephonyManager telephonyManager;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAppPermissions(new String[]{
                        Manifest.permission.READ_PHONE_STATE},

                R.string.msg, REQUEST_PERMISSION);
        IEmail = findViewById(R.id.email);
        IPass = findViewById(R.id.password);
        L = findViewById(R.id.login);
        RG = findViewById(R.id.register);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {

            finish();

            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));


        }

        L.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        RG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserReg();
            }
        });
    }


    private void userLogin(){
        String email = IEmail.getText().toString().trim();
        String password  = IPass.getText().toString().trim();


        if(TextUtils.isEmpty(email)){
            IEmail.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            IPass.setError("Please enter password");
            return;
        }





        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

                        }else{

                            Toast.makeText(MainActivity.this,"The email or password is incorrect",Toast.LENGTH_LONG).show();
                        }


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

    private void UserReg(){
        final String email = IEmail.getText().toString().trim();
        final String password  = IPass.getText().toString().trim();


        if(TextUtils.isEmpty(email)){
            IEmail.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            IPass.setError("Please enter password");
            return;
        }


        if (password.length() < 7) {
            Toast.makeText(this, "Password too short, enter minimum 7 characters!", Toast.LENGTH_LONG).show();
            return;
        }


        final String name   = email.substring(0, email.lastIndexOf("@"));
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference NRef = rootRef.child("UserId");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                assert android_id !=null;
                if (dataSnapshot.child(android_id).exists()) {

                    String email = dataSnapshot.child(android_id).getValue(String.class);
                    assert email != null;
                    new AlertDialog.Builder(MainActivity.this)
                            .setCancelable(false)
                            .setTitle("Warning")
                            .setMessage("This device is connected with this email : " +email)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                }
                            })
                            .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .show();
                } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                    rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Coins").setValue(0);
                                    rootRef.child("UserId").child(android_id).setValue(email);

                                    if (name.contains(".")) {
                                        String m = name.replace(".", "");
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(m);
                                    } else {
                                        rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("UserName").setValue(name);
                                    }

                                    rootRef.child("UserName").child(name).setValue(firebaseAuth.getCurrentUser().getUid());
                                    rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Pass").setValue(password);
                                    rootRef.child(firebaseAuth.getCurrentUser().getUid()).child("Email").setValue(email);
                                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    finish();
                                } else {

                                    Toast.makeText(MainActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                                }


                            }
                        });

            }


            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        NRef.addListenerForSingleValueEvent(eventListener);


    }


}
