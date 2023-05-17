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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import edu.cuhk.csci3310.cufood.model.CanteenManager;

public class loginManagerActivity extends AppCompatActivity {
    private static final String TAG = "LogInManager";
    EditText canteenID, password;
    Button login, loginManager;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managerlogin);
        mAuth = FirebaseAuth.getInstance();
        canteenID = (EditText) findViewById(R.id.canteenID);
        password = (EditText) findViewById(R.id.canteenpassword);
        login = (Button) findViewById(R.id.log_in_return);
        loginManager = (Button) findViewById(R.id.log_in);
//
//        // handle the login button part to return to normal customer login activity
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // go to log in activity
//                Intent intent = new Intent(loginManagerActivity.this, loginActivity.class);
//                startActivity(intent);
//            }
//        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference canteenAdminRef = db.collection("canteen_admins");

        // handle the login part for the canteen manager
        // first we have to add the canteen IDs to the database in the first run of this code part
//        CanteenManager manager1 = new CanteenManager("uccoffee@gmail.com", "uccoffeee", "4HRR4tKLylhfzjdtJu90");
//        CanteenManager manager2 = new CanteenManager("cwcstaffcom@gmail.com", "cwcstaffcom", "85jLUFlzy74j2XlAcpen");
//        CanteenManager manager3 = new CanteenManager("ccstaff@gmail.com", "ccstaff", "8IA7XtWDULbPghWd9Huj");
//        CanteenManager manager4 = new CanteenManager("uccafe@gmail.com", "uccafe", "EEeDNgRniXasUNkM3WTD");
//        CanteenManager manager5 = new CanteenManager("shawstaff@gmail.com", "shawstaff", "I5kTNBnONv4zTwKkRFWp");
//        CanteenManager manager6 = new CanteenManager("cwcstaff@gmail.com", "cwcstaff", "It21v246fj98AktGgXDr");
//        CanteenManager manager7 = new CanteenManager("wys@gmail.com", "wyscan", "aBMGV3g9TSCUbbMt5TuX");
//        CanteenManager manager8 = new CanteenManager("na@gmail.com", "nacanteen", "rinBCTLKvGH7m2bvVsQ8");
//        CanteenManager manager9 = new CanteenManager("mc@gmail.com", "mccanteen", "vQJa4OnDr7xAplhVNhoJ");
//        CanteenManager manager10 = new CanteenManager("lws@gmail.com", "lwscan", "bDpk3GJqCyXnKA7lTQ9d");
        // signing the managers for authentication registration purpose

//        db.collection("canteen_admins")
//                .add(manager7)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding the user", e);
//                    }
//                });
//        // adding manager 2
//        db.collection("canteen_admins")
//                .add(manager8)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding the user", e);
//                    }
//                });

        // adding manager 3
//        db.collection("canteen_admins")
//                .add(manager10)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error adding the user", e);
//                    }
//                });



        // DO NOT DELETE
//        // signing up the managers to the app - once only
//        loginManager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String canteenIDStr = canteenID.getText().toString();
//                final String passwordStr = password.getText().toString();
//                mAuth.createUserWithEmailAndPassword(canteenIDStr, passwordStr)
//                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign in success, update UI with the signed-in user's information
//                                    Log.d(TAG, "createUserWithEmail:success");
//                                    FirebaseUser user = mAuth.getCurrentUser();
//                                } else {
//                                    // If sign in fails, display a message to the user.
//                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                                    Toast.makeText(loginManagerActivity.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//            }
//        });


        // logging in the canteen managers
        loginManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String canteenIDStr = canteenID.getText().toString();
                final String passwordStr = password.getText().toString();
                mAuth.signInWithEmailAndPassword(canteenIDStr, passwordStr)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    // check if user exists in database
                                    // Query the database for the provided username
                                    canteenAdminRef.whereEqualTo("canteenID", canteenIDStr)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean matchFound = false;
                                                        // Check if any document matches the provided username
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String retrievedPassword = document.getString("password");
                                                            String restaurantId = document.getString("restaurantId");
                                                            if (retrievedPassword.equals(passwordStr)) {
                                                                // The passwords match, login the user
                                                                FirebaseUser user = mAuth.getCurrentUser();
                                                                Toast.makeText(loginManagerActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(loginManagerActivity.this, MenuCategoryActivity.class);
                                                                intent.putExtra(MenuCategoryActivity.KEY_RESTAURANT_ID, restaurantId);
                                                                startActivity(intent);
                                                                finish();
                                                                matchFound = true;
                                                                break;
                                                                //return;
                                                            }
                                                        }
                                                        if (!matchFound) {
                                                            // No matching document found in database, so login failed
                                                            Toast.makeText(loginManagerActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                                        }

                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Toast.makeText(loginManagerActivity.this, "Authentication failed.",
                                                                Toast.LENGTH_SHORT).show();
                                                        //Toast.makeText(loginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    // If sign in fails, display error message to the user.
                                    Toast.makeText(loginManagerActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                               // }
                            }
                        });

            }
       });

    }
}
