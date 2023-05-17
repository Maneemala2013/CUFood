package edu.cuhk.csci3310.cufood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class loginActivity extends AppCompatActivity {

    private static final String TAG = "LogIn";
    public static final String KEY_USER_ID = "key_user_id";
    EditText username, password,email;
    Button signup, login, loginManager;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password = (EditText) findViewById(R.id.password);
        signup = (Button) findViewById(R.id.sign_up);
        login = (Button) findViewById(R.id.log_in);
        loginManager = (Button) findViewById(R.id.log_in_manager);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference customersRef = db.collection("customers");

        // handle the login button for general users (not canteen managers)
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String usernameStr = username.getText().toString();
                final String passwordStr = password.getText().toString();
                final String emailStr = email.getText().toString();

                mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    // check if user exists in database
                                    // Query the database for the provided username
                                    customersRef.whereEqualTo("username", usernameStr)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean matchFound = false;
                                                        // Check if any document matches the provided username
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String retrievedPassword = document.getString("password");
                                                            if (retrievedPassword.equals(passwordStr)) {
                                                                // The passwords match, login the user
                                                                //Log.d(TAG, "signInWithEmail:success");
                                                                FirebaseUser user = mAuth.getCurrentUser();
                                                                String userId = document.getId();
                                                                Toast.makeText(loginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                                                intent.putExtra(loginActivity.KEY_USER_ID, userId);
                                                                startActivity(intent);
                                                                finish();
                                                                matchFound = true;
                                                                break;
                                                               // return;
                                                            }
                                                        }
                                                        if (!matchFound) {
                                                            // No matching document found in database, so login failed
                                                            Toast.makeText(loginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                                        }


                                                    } else {
                                                        Toast.makeText(loginActivity.this, "Authentication failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                        Toast.makeText(loginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(loginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }


                            /*   else {
                                   // error accessing firebase - permission denied
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    if (task.getException() instanceof FirebaseFirestoreException) {
                                        FirebaseFirestoreException exception = (FirebaseFirestoreException) task.getException();
                                        if (exception.getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED) {
                                            Toast.makeText(loginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }*/
                                    // clearing the username and password fields for further logins
                                  //  username.setText("");
                                    //password.setText("");
                               // }
                            }
                        });
            }
        });

        // handle the sign up part
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to sign up activity
                Intent intent = new Intent(loginActivity.this, signupActivity.class);
                startActivity(intent);
            }
        });

        // handle the log-in canteen manager part
        loginManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to log in for managers activity
                Intent intent = new Intent(loginActivity.this, loginManagerActivity.class);
                startActivity(intent);
            }
        });



    }


}
