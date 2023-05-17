package edu.cuhk.csci3310.cufood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.model.Restaurant;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
//import edu.cuhk.csci3310.cufood.databinding.ActivityCanteenNavigationBinding;

public class CanteenSettingActivity extends AppCompatActivity {

//    @BindView(R.id.canteen_bottom_navigation)
//    BottomNavigationView navigationBar;

//    private ActivityCanteenNavigationBinding binding;
    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    private FirebaseFirestore mFirestore;
    private DocumentReference mCanteenReference;
    private String restaurantId;
    private static final String TAG = "CanteenSetting";
    private String name = "", category = "", location = "", photo = "";
    private double rating = 0;
    int SELECT_PICTURE = 200;
    private Uri selectedImageUri = null;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.setting_category)
    EditText mEditCategory;

    @BindView(R.id.setting_name)
    EditText mEditName;

    @BindView(R.id.setting_location)
    EditText mEditLocation;

    @BindView(R.id.setting_image)
    ImageView mCanteenImage;

    @BindView(R.id.profile_form_submit)
    Button mButtonSubmit;

    @BindView(R.id.profile_form_cancel)
    Button mButtonCancel;

    @BindView(R.id.setting_select_image)
    Button mButtonSelectImage;

    @BindView(R.id.setting_rating)
    MaterialRatingBar mRatingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen_setting);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();

        restaurantId = intent.getExtras().getString(KEY_RESTAURANT_ID);
        name = intent.getExtras().getString("name");
        category = intent.getExtras().getString("category");
        location = intent.getExtras().getString("city");
        photo = intent.getExtras().getString("photo");
        rating = intent.getExtras().getDouble("avgRating");
        System.out.println("name: " + name);
        System.out.println("category: " + category);
        System.out.println("location: " + location);
        System.out.println("photo: " + photo);

        setFieldNames();
        mRatingIndicator.setRating((float) rating);

        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }

        mFirestore = FirebaseFirestore.getInstance();
        mCanteenReference = mFirestore.collection("restaurants").document(restaurantId);


        mButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (mEditName != null) {
                    name = mEditName.getText().toString();
                }
                if (mEditCategory != null) {
                    category = mEditCategory.getText().toString();
                }
                if (mEditLocation != null) {
                    location = mEditLocation.getText().toString();
                }

                if(selectedImageUri != null) {
                    photo = selectedImageUri.toString();
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", name);
                updates.put("category", category);
                updates.put("city", location);
                updates.put("photo", photo);

                mCanteenReference.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Document updated successfully!" + name);
                                getFieldNames();
                                setFieldNames();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                setFieldNames();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFieldNames();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


        mButtonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an instance of the
                // intent of the type image
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                // pass the constant to compare it
                // with the returned requestCode
                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });

//        // Bottom Navigation Bar
//        navigationBar.setSelectedItemId(R.id.canteen_setting);
//        navigationBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.menu_management:
//                        startActivity(new Intent(getApplicationContext(), MenuCategoryActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.order_management:
//                        startActivity(new Intent(getApplicationContext(), OrderActivity.class));
//                        overridePendingTransition(0, 0);
//                        return true;
//                    case R.id.canteen_setting:
//                        return true;
//                }
//                return false;
//            }
//        });

//        binding = ActivityCanteenNavigationBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById();
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_canteen_navigation);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_canteen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
//        String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        switch (item.getItemId()) {
            case R.id.menu_management:
                intent = new Intent(this, MenuCategoryActivity.class);
                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
                startActivity(intent);
                break;
            case R.id.order_management:
                intent = new Intent(this, OrderActivity.class);
                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
                startActivity(intent);
                break;
            case R.id.canteen_setting:
                break;
            case R.id.canteen_sign_out:
                AuthUI.getInstance().signOut(this);
                //startSignIn();
                // go to homepage
                intent = new Intent(CanteenSettingActivity.this, homepage.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getFieldNames() {

        mCanteenReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            name = documentSnapshot.getString("name");
                            category = documentSnapshot.getString("category");
                            location = documentSnapshot.getString("city");
                            photo = documentSnapshot.getString("photo");
                            Log.d(TAG, "Get field names: Name: " + name + "\nCategory: " + category);
                        } else {
                            Log.d(TAG, "Document does not exist");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });
    }

    private void setFieldNames() {
        mEditName.setText(name);
        mEditCategory.setText(category);
        mEditLocation.setText(location);
        Glide.with(this).load(photo).into(mCanteenImage);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                Log.d("selectedImageURi", String.valueOf(selectedImageUri));
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    mCanteenImage.setImageURI(selectedImageUri);
                }
            }
        }
    }
}