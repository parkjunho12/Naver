package com.timeline.naver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText user_id;
    private EditText user_pw;
    SharedPreferences sharedPreferences;
    private UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user_id =findViewById(R.id.editid);
        user_pw =findViewById(R.id.editpass);
        Button loginBtn =(Button)findViewById(R.id.loginbtn);
        loginBtn.setOnClickListener(loginClick);
        Button regbtn =(Button)findViewById(R.id.regbtn);
        regbtn.setOnClickListener(regClick);
        sharedPreferences = getSharedPreferences("junho", Activity.MODE_PRIVATE);
        String id = sharedPreferences.getString("user_id", "");
        if (!"".equals(id)) {
            user_id.setText(id);
        }

    }
Button.OnClickListener regClick =new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), RegActivity.class);
        startActivity(intent);
    }
};

    Button.OnClickListener loginClick = new View.OnClickListener() {
        public void onClick(View view) {
            if (!validateForm()) return;

            FirebaseAuth.getInstance().signInWithEmailAndPassword(user_id.getText().toString(), user_pw.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sharedPreferences.edit().putString("user_id", user_id.getText().toString()).commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        getUserInfoFromServer();
                        startActivity(intent);
                        finish();
                    } else {
                        Util9.showMessage(getApplicationContext(), task.getException().getMessage());
                    }
                }
            });
        }
    };

    void getUserInfoFromServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userModel = documentSnapshot.toObject(UserModel.class);
                Static_setting.ID = userModel.getUserid();
                Static_setting.Name = userModel.getRealname();
                Static_setting.Phone = userModel.getPhone();

            }
        });
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
