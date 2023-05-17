package edu.cuhk.csci3310.cufood;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.adapter.MenuCustomerAdapter;
import edu.cuhk.csci3310.cufood.model.Restaurant;
import edu.cuhk.csci3310.cufood.util.RestaurantUtil;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class MenuCustomerActivity extends AppCompatActivity {

    private static final String TAG = "MenuCustomerActivity";
    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    public static final String KEY_USER_ID = "key_user_id";

    @BindView(R.id.restaurant_image)
    ImageView mImageView;

    @BindView(R.id.restaurant_name)
    TextView mNameView;

    @BindView(R.id.restaurant_rating)
    MaterialRatingBar mRatingIndicator;

    @BindView(R.id.restaurant_num_ratings)
    TextView mNumRatingsView;


    @BindView(R.id.restaurant_category)
    TextView mCategoryView;

    @BindView(R.id.restaurant_price)
    TextView mPriceView;

    @BindView(R.id.recycler_menu_items)
    RecyclerView mMenuRecycler;

    @BindView(R.id.button_shopping_cart)
    FloatingActionButton mButtonCart;

    @BindView(R.id.fab_show_rating_dialog)
    FloatingActionButton mButtonRating;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private MenuCustomerAdapter mAdapter;
    private DocumentReference mMenuRef;
    private Restaurant mRestaurant;
//    private DocumentReference mCustomerRef;
    private String customerId;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_customer);
        ButterKnife.bind(this);

        // Get customer ID from extras
        customerId = getIntent().getExtras().getString(KEY_USER_ID);
        if (customerId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_USER_ID);
        }

        // Get restaurant ID from extras
        restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }

        FirebaseFirestore.setLoggingEnabled(true);

        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mMenuRef = mFirestore.collection("restaurants").document(restaurantId);
        // Get reference to the customer
//        mCustomerRef = mFirestore.collection("customers").document(customerId);

        // Get menu itemsRef.whereEqualTo("isSelected", true);
        mQuery= mMenuRef
                .collection("menu")
                .whereEqualTo("availableStatus", true)
                .orderBy("menuName", Query.Direction.ASCENDING);

        initRecyclerView();

        mButtonCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCustomerActivity.this, ShoppingCartActivity.class);
                intent.putExtra(MenuCustomerActivity.KEY_USER_ID, customerId);
                intent.putExtra(MenuCustomerActivity.KEY_RESTAURANT_ID, restaurantId);
                startActivity(intent);
            }
        });

        mButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuCustomerActivity.this, RestaurantDetailActivity.class);
                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
                startActivity(intent);
            }
        });

        mMenuRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mRestaurant = document.toObject(Restaurant.class);
                        onRestaurantLoaded(mRestaurant);
                    } else {
                        Log.w(TAG, "Error getting the restaurant model");
                    }
                } else {
                    Log.w(TAG, "Error task not successful in getting documentSnapshot for restaurant model");
                }
            }
        });

    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new MenuCustomerAdapter(mQuery, mFirestore, customerId) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                super.onError(e);
            }
        };


        mMenuRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMenuRecycler.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CollectionReference collectionReference = mFirestore.collection("customers").document(customerId).collection("shopping_cart");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Create a batch operation to delete each document
                    WriteBatch batch = mFirestore.batch();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        batch.delete(document.getReference());
                    }

                    // Execute the batch operation to delete all documents
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.w(TAG, "Successfully deleted everything from Firebase collection");
                            } else {
                                Log.w(TAG, "Error deleting the meals from shopping cart");
                            }
                        }
                    });
                } else {
                    Log.w(TAG, "Error deleting the meals from shopping cart");
                }
            }
        });
    }


    private void onRestaurantLoaded(Restaurant restaurant) {
        mNameView.setText(restaurant.getName());
        mRatingIndicator.setRating((float) restaurant.getAvgRating());
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings, restaurant.getNumRatings()));
        //mCityView.setText(restaurant.getCity());
        mCategoryView.setText(restaurant.getCategory());
        mPriceView.setText(RestaurantUtil.getPriceString(restaurant));

        // Background image
        Glide.with(mImageView.getContext())
                .load(restaurant.getPhoto())
                .into(mImageView);
    }

}