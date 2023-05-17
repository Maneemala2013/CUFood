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
import java.util.Date;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.adapter.OrderAdapter;
import edu.cuhk.csci3310.cufood.model.Order;

public class OrderActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, OrderAdapter.OnOrderListener, edu.cuhk.csci3310.cufood.AddRiderFragment.FinishOrderListener {

    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    private static final String TAG = "OrderActivity";
    private String name = "", category = "", location = "", photo = "";
    private double rating = 0;

    final long delayMillis=200000; // 200 seconds
    Handler h = null;
    Runnable r;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pending_order_recyclerview)
    RecyclerView mPendingRecycler;

    @BindView(R.id.completed_order_recyclerview)
    RecyclerView mCompletedRecycler;

    @BindView(R.id.view_empty_order)
    ViewGroup mEmptyView;

    private FirebaseFirestore mFirestore;
    private DocumentReference mRestaurantRef;
    private ListenerRegistration mRestaurantRegistration;

    private ViewOrderFragment mViewOrderDialog;
    private AddRiderFragment mAddRiderDialog;

    private OrderAdapter mPendingOrderAdapter;
    private OrderAdapter mCompletedOrderAdapter;

    private String restaurantId;
    private Date currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        // Get restaurant ID from extras
        restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mRestaurantRef = mFirestore.collection("restaurants").document(restaurantId);

        //get the field names for canteen setting
        getFieldNames();

        // Get pending orders in the selected canteen
        Query pendingOrderQuery = mRestaurantRef
                .collection("orders")
                .whereEqualTo("finishedStatus", false)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        Query completedOrderQuery = mRestaurantRef
                .collection("orders")
                .whereEqualTo("finishedStatus", true)
                .orderBy("timestamp", Query.Direction.DESCENDING);;

        // RecyclerView
        mPendingOrderAdapter = new OrderAdapter(pendingOrderQuery, this) {
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

        mCompletedOrderAdapter = new OrderAdapter(completedOrderQuery, this) {
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

        /* Refresh the page every 200 seconds */
        // Ref.: https://stackoverflow.com/questions/29745836/restart-an-activity-on-a-particular-time
        currentTime = new Date();
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.SECOND, 200);

        final Date timePassed = c1.getTime();
        h=new Handler(Looper.getMainLooper());
        r = new Runnable() {

            public void run() {

                //current time
                Date now = new Date();

                //comparing current time with timePassed (after 200s)
                if(now.compareTo(timePassed) > 0){
                    //restarting the activity
//                    currentTime = new Date();
//                    overridePendingTransition(0, 0);
//                    finish();
//                    overridePendingTransition(0, 0);
//                    startActivity(getIntent());
//                    overridePendingTransition(0, 0);
                    recreate();
                }
                h.postDelayed(this, delayMillis);
            }
        };

        h.post(r);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_canteen, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        switch (item.getItemId()) {
            case R.id.menu_management:
                intent = new Intent(this, MenuCategoryActivity.class);
                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
                startActivity(intent);
                break;
            case R.id.order_management:
                break;
            case R.id.canteen_setting:
                intent = new Intent(this, CanteenSettingActivity.class);
                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
                System.out.println("MenuCategory");
                System.out.println("name: " + name);
                System.out.println("category: " + category);
                System.out.println("location: " + location);
                intent.putExtra("name", name);
                intent.putExtra("category", category);
                intent.putExtra("city", location);
                intent.putExtra("photo", photo);
                intent.putExtra("avgRating", rating);
                startActivity(intent);
                break;
            case R.id.canteen_sign_out:
                AuthUI.getInstance().signOut(this);
                //startSignIn();
                // go to homepage
                intent = new Intent(OrderActivity.this, homepage.class);
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

    private Task<Void> finishOrder(final DocumentReference restaurantRef, String orderId, Date finishedTime, String riderDetails, boolean delivery, String customerId, String customerOrderId) {
        //Update order status in both customer and restaurant sides
        DocumentReference customerRef = mFirestore.collection("customers").document(customerId);
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
            if(customerOrderId != null) {
                customerRef.collection("orders")
                        .document(customerOrderId).update("finishedCookTime", finishedTime, "riderInfo", riderDetails, "finishedStatus", true).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Finish the order and add rider info.: Update customer side");
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Finish order failed: Update customer side", e);

                                // Show failure message and hide keyboard
                                hideKeyboard();
                                Snackbar.make(findViewById(android.R.id.content), "Failed to delete order: Update customer side",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
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
                            Snackbar.make(findViewById(android.R.id.content), "Failed to finish order",
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    });
            if(customerOrderId != null) {
                customerRef.collection("orders")
                        .document(customerOrderId).update("finishedCookTime", finishedTime, "finishedStatus", true).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Finish the order: Update customer side");
                            }
                        })
                        .addOnFailureListener(this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Finish order failed: Update customer side", e);

                                // Show failure message and hide keyboard
                                hideKeyboard();
                                Snackbar.make(findViewById(android.R.id.content), "Failed to finish order: Update customer side",
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }

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
    public void onFinishOrder(DocumentSnapshot order, Order orderToBeFinished) {
        mAddRiderDialog = AddRiderFragment.newInstance(orderToBeFinished, order.getId(), orderToBeFinished.getUserId(), orderToBeFinished.getCustomerOrderId());
        mAddRiderDialog.show(getSupportFragmentManager(), AddRiderFragment.TAG);
    }

    @Override
    public void onDeleteOrder(final DocumentSnapshot orderToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Order History")
                .setMessage("Do you want to delete this order history?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteOrder(mRestaurantRef, orderToDelete);
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

    @Override
    public void onFinishRiderOrder(String orderId, Date finishedTime, String riderDetails, boolean delivery, String customerId, String customerOrderId) {
        finishOrder(mRestaurantRef, orderId, finishedTime, riderDetails, delivery, customerId, customerOrderId);
    }

    private void getFieldNames() {

        mRestaurantRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            name = documentSnapshot.getString("name");
                            category = documentSnapshot.getString("category");
                            location = documentSnapshot.getString("city");
                            photo = documentSnapshot.getString("photo");
                            rating = documentSnapshot.getDouble("avgRating");
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
}