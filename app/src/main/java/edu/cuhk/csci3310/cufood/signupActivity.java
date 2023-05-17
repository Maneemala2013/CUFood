package edu.cuhk.csci3310.cufood;

import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import edu.cuhk.csci3310.cufood.model.User;

public class signupActivity extends AppCompatActivity {
    private static final String TAG = "SignUp";
    EditText username, email,password;
    Button signup, login;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        mAuth = FirebaseAuth.getInstance();
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.sign_up);
        login = (Button) findViewById(R.id.log_in);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference customersRef = db.collection("customers");



        // Adding some users to the database in the first run
        //User user1 = new User("User1", "user1@gmail.com", "user1");
        //User user2 = new User("User2", "user2@gmail.com", "user2");
        //User user3 = new User("User3", "user3@gmail.com", "user3");

        // Add a new document with a generated ID to the "customer" collection
       /* db.collection("customers")
                .add(user1)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding the user", e);
                    }
                });

        // adding user2
        db.collection("customers")
                .add(user2)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding the user", e);
                    }
                });

        // adding user3
        db.collection("customers")
                .add(user3)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding the user", e);
                    }
                });
*/

        // registering users when clicking on sign up button
        // reference:https://firebase.google.com/docs/auth/android/start#java_4
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String usernameStr = username.getText().toString();
                final String emailStr = email.getText().toString();
                final String passwordStr = password.getText().toString();
                // messages as reminders if one information is missing
                if (TextUtils.isEmpty(emailStr)){
                    Toast.makeText(signupActivity.this, "Input Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(usernameStr)){
                    Toast.makeText(signupActivity.this, "Input username",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordStr)){
                    Toast.makeText(signupActivity.this, "Input valid password",Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    // adding user to database
                                    // check if the user exists already in the database or not
                                    // Check if the username and email already exist in the database
                                    Query query = customersRef.whereEqualTo("username", usernameStr)
                                            .whereEqualTo("email", emailStr);
                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (!task.getResult().isEmpty()) {
                                                    // User already exists
                                                    Toast.makeText(signupActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // user does not exist so we register him/ her
                                                    // creating user object to add to the firebase database
                                                    User user = new User(usernameStr, emailStr, passwordStr);
                                                    // Adding user object to the "customers" collection
                                                    customersRef.add(user)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                                    Toast.makeText(signupActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                                                    // go to mainActivity
                                                                    Intent intent = new Intent(signupActivity.this, MainActivity.class);
                                                                    intent.putExtra(loginActivity.KEY_USER_ID, documentReference.getId());
                                                                    startActivity(intent);
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error adding document", e);
                                                                    Toast.makeText(signupActivity.this, "Sign up failed!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }


                                            }
                                        }
                                    });
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(signupActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });








            }
        });

        // handle the login button part
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to sign up activity
                Intent intent = new Intent(signupActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });
    }
}
