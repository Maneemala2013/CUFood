package edu.cuhk.csci3310.cufood;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.adapter.CustomerOrderAdapter;
import edu.cuhk.csci3310.cufood.adapter.OrderAdapter;
import edu.cuhk.csci3310.cufood.model.Order;

public class CustomerOrderActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, CustomerOrderAdapter.OnOrderListener {

    private static final String TAG = "CustomerOrderActivity";
//    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_ORDER_ID = "key_order_id";

    @BindView(R.id.customer_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.customer_pending_recyclerview)
    RecyclerView mPendingRecycler;

    @BindView(R.id.customer_completed_recyclerview)
    RecyclerView mCompletedRecycler;

    @BindView(R.id.customer_view_empty)
    ViewGroup mEmptyView;

    private FirebaseFirestore mFirestore;
    private DocumentReference mCustomerRef;

    private ViewOrderFragment mViewOrderDialog;

    private CustomerOrderAdapter mPendingOrderAdapter;
    private CustomerOrderAdapter mCompletedOrderAdapter;

    private String customerId;
//    private String customerOrderId;
    private Date currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        // Get customer ID from extras
        customerId = getIntent().getExtras().getString(KEY_USER_ID);
//        customerOrderId = getIntent().getExtras().getString(KEY_ORDER_ID);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mCustomerRef = mFirestore.collection("customers").document(customerId);

        // Get reference to the restaurant
//        mRestaurantRef = mFirestore.collection("restaurants").document(restaurantId);

        // Get pending orders in the selected canteen
        Query pendingOrderQuery = mCustomerRef
                .collection("orders")
                .whereEqualTo("finishedStatus", false)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        Query completedOrderQuery = mCustomerRef
                .collection("orders")
                .whereEqualTo("finishedStatus", true)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // RecyclerView
        mPendingOrderAdapter = new CustomerOrderAdapter(pendingOrderQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mPendingRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mPendingRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mCompletedOrderAdapter = new CustomerOrderAdapter(completedOrderQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mCompletedRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mCompletedRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mPendingRecycler.setLayoutManager(new LinearLayoutManager(this));
        mPendingRecycler.setAdapter(mPendingOrderAdapter);
        mCompletedRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCompletedRecycler.setAdapter(mCompletedOrderAdapter);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_for_canteen, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent;
//        String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
//        switch (item.getItemId()) {
//            case R.id.menu_management:
//                intent = new Intent(this, MenuCategoryActivity.class);
//                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
//                startActivity(intent);
//                break;
//            case R.id.order_management:
//                break;
//            case R.id.canteen_setting:
//                intent = new Intent(this, CanteenSettingActivity.class);
//                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
//                startActivity(intent);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.menu_add_items:
//                onAddItemsClicked();
//                break;
            case R.id.canteen_overview:
                Intent intent = new Intent(CustomerOrderActivity.this, MainActivity.class);
                intent.putExtra(loginActivity.KEY_USER_ID, customerId);
                startActivity(intent);
                break;
            case R.id.order_status:
                break;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this);
                //startSignIn();
                // go to homepage
                intent = new Intent(CustomerOrderActivity.this, homepage.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPendingOrderAdapter.startListening();
        mCompletedOrderAdapter.startListening();
//        mRestaurantRegistration = mRestaurantRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPendingOrderAdapter.stopListening();
        mCompletedOrderAdapter.stopListening();
//        if (mRestaurantRegistration != null) {
//            mRestaurantRegistration.remove();
//            mRestaurantRegistration = null;
//        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "order:onEvent", e);
            return;
        }
    }

    private Task<Void> finishOrder(final DocumentReference restaurantRef, String orderId, Date finishedTime, String riderDetails, boolean delivery) {
        if (delivery) {
            restaurantRef.collection("orders")
                    .document(orderId).update("finishedCookTime", finishedTime, "riderInfo", riderDetails, "finishedStatus", true).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Finish the order and add rider info.");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Finish order failed", e);

                            // Show failure message and hide keyboard
                            hideKeyboard();
                            Snackbar.make(findViewById(android.R.id.content), "Failed to delete order",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            restaurantRef.collection("orders")
                    .document(orderId).update("finishedCookTime", finishedTime, "finishedStatus", true).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Finish the order");
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Finish order failed", e);

                            // Show failure message and hide keyboard
                            hideKeyboard();
                            Snackbar.make(findViewById(android.R.id.content), "Failed to delete order",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
        return null;
    }

    private Task<Void> deleteOrder(final DocumentReference restaurantRef, final DocumentSnapshot order) {
        // Create a reference for new rating, for use inside the transaction
        restaurantRef.collection("orders")
                .document(order.getId()).delete().addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Order deleted");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Delete order failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to delete order",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        return null;
    }


    @Override
    public void onViewOrder(DocumentSnapshot order, Order orderToBeViewed) {
        mViewOrderDialog = ViewOrderFragment.newInstance(orderToBeViewed);
        mViewOrderDialog.show(getSupportFragmentManager(), ViewOrderFragment.TAG);
    }

    @Override
    public void onDeleteOrder(final DocumentSnapshot orderToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Order History")
                .setMessage("Do you want to delete this order history?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOrder(mCustomerRef, orderToDelete);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}