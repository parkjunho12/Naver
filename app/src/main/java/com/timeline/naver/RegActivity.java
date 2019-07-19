package com.timeline.naver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class RegActivity extends AppCompatActivity {
    private EditText user_id;
    private EditText user_pw;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        user_id = (EditText) findViewById(R.id.ID);
        user_pw = (EditText) findViewById(R.id.PW);
        final EditText ed_Name = (EditText) findViewById(R.id.Name);
        final EditText ed_Phone = (EditText) findViewById(R.id.Phone);
        Button regbtn =(Button) findViewById(R.id.Register_btn);
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateForm()) return;
                final String id = user_id.getText().toString();

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(id, user_pw.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sharedPreferences.edit().putString("user_id", id).commit();
                            final String uid = FirebaseAuth.getInstance().getUid();

                            UserModel userModel = new UserModel();
                            userModel.setUid(uid);
                            userModel.setUserid(id);
                            userModel.setUsernm(extractIDFromEmail(id));
                            userModel.setUsermsg("...");
                            userModel.setRealname(ed_Name.getText().toString());
                            userModel.setPhone(ed_Phone.getText().toString());

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            Log.e("존나 ",uid+":uid  "+userModel.getToken()+"token"+userModel.getUserid()+"id");
                            db.collection("users").document(uid)
                                    .set(userModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {




                                            Static_setting.ID =user_id.getText().toString();
                                            Static_setting.PW =user_pw.getText().toString();
                                            Static_setting.Name =ed_Name.getText().toString();
                                            Static_setting.Phone =ed_Phone.getText().toString();


                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            sendRegistrationToServer();
                                            startActivity(intent);
                                            Log.d(String.valueOf(R.string.app_name), "DocumentSnapshot added with ID: " + uid);
                                        }
                                    });
                        } else {
                            Util9.showMessage(getApplicationContext(), task.getException().getMessage());
                        }
                    }
                });


            }
        });
        sharedPreferences = getSharedPreferences("junho", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id", "");
        if (!"".equals(id)) {
            user_id.setText(id);
        }

    }
    String extractIDFromEmail(String email){
        String[] parts = email.split("@");
        return parts[0];
    }
    void sendRegistrationToServer() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        FirebaseFirestore.getInstance().collection("users").document(uid).set(map, SetOptions.merge());
    }
    private boolean validateForm() {
        boolean valid = true;

        String email = user_id.getText().toString();
        if (TextUtils.isEmpty(email)) {
            user_id.setError("Required.");
            valid = false;
        } else {
            user_id.setError(null);
        }

        String password = user_pw.getText().toString();
        if (TextUtils.isEmpty(password)) {
            user_pw.setError("Required.");
            valid = false;
        } else {
            user_pw.setError(null);
        }

        return valid;
    }

}
