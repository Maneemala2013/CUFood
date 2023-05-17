package edu.cuhk.csci3310.cufood;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import edu.cuhk.csci3310.cufood.adapter.ShoppingCartAdapter;

public class ShoppingCartActivity extends AppCompatActivity implements ShoppingCartAdapter.OnDataChangedListener{

    private static final String TAG = "ShoppingCartActivity";
    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    public static final String KEY_USER_ID = "key_user_id";
    private List<String> foodName = new ArrayList<>();;
    private List<Integer> foodAmount = new ArrayList<>();;

    // key: foodName, value: Amount
    private LinkedHashMap<String, Integer> orderList = new LinkedHashMap<String, Integer>();
    private final double[] totalPrice = {0};

    @BindView(R.id.cart_recycler)
    RecyclerView mCartRecycler;

    @BindView(R.id.total_text)
    TextView mTotal;

    @BindView(R.id.deli_address)
    EditText mDeliveryAddress;

    @BindView(R.id.choice_2)
    RadioButton choice_2; // delivery

    @BindView(R.id.notes_to_canteen)
    EditText mNotes;

    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private ShoppingCartAdapter mAdapter;
    private CollectionReference mCartRef;
    private DocumentReference mCustomerRef;
    private String customerId;
    private String restaurantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
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

        Intent intent = getIntent();

        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the cart
        mCartRef = mFirestore.collection("customers").document(customerId).collection("shopping_cart");

        // Get reference to the customer
        mCustomerRef = mFirestore.collection("customers").document(customerId);


        // Get menu itemsRef.whereEqualTo("isSelected", true);
        mQuery= mCartRef.orderBy("menuName", Query.Direction.ASCENDING);

        // Query the collection and loop through each document
        mCartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Access the price field of each document and add it to the total price
                        Double price = document.getDouble("price");
                        if (price != null) {
                            totalPrice[0] += price;
                        }
                    }

                    // Log the total price to the console
                    System.out.println("Total price: " + totalPrice[0]);
                    mTotal.setText("Total: " + totalPrice[0]);
                } else {
                    // Handle errors
                    System.out.println("Error getting collection: " + task.getException().getMessage());
                }
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");
        }

        mAdapter = new ShoppingCartAdapter(mQuery, mFirestore, ShoppingCartActivity.this, customerId) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                super.onError(e);
            }
        };

        mCartRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCartRecycler.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAdapter != null) {
            mAdapter.startListening();
        }

        final double[] totalPrice = {0};

        mCartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Access the price field of each document and add it to the total price
                        Double price = document.getDouble("price");
                        if (price != null) {
                            totalPrice[0] += price;
                        }
                        // get Food Order Summary
                        String foodName = document.getString("menuName");
                        if (!orderList.containsKey(foodName)) {
                            orderList.put(foodName, 1);
                        }
                        else {
                            int currentAmount = orderList.get(foodName);
                            orderList.put(foodName, currentAmount+1);
                        }
                    }

                    // Log the total price to the console
                    System.out.println("Total price: " + totalPrice[0]);
                    mTotal.setText("Total: " + totalPrice[0]);
                } else {
                    // Handle errors
                    System.out.println("Error getting collection: " + task.getException().getMessage());
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onDataChanged() {
        Log.w(TAG, "Data changed!");
        final double[] totalPrice = {0};

        mCartRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Access the price field of each document and add it to the total price
                        Double price = document.getDouble("price");
                        if (price != null) {
                            totalPrice[0] += price;
                        }
                        // get Food Order Summary
                        String foodName = document.getString("menuName");
                        if (!orderList.containsKey(foodName)) {
                            orderList.put(foodName, 1);
                        }
                        else {
                            int currentAmount = orderList.get(foodName);
                            orderList.put(foodName, currentAmount+1);
                        }
                    }

                    // Log the total price to the console
                    System.out.println("Total price: " + totalPrice[0]);
                    mTotal.setText("Total: " + totalPrice[0]);
                } else {
                    // Handle errors
                    System.out.println("Error getting collection: " + task.getException().getMessage());
                }
            }
        });
    }

    @OnCheckedChanged({R.id.choice_1, R.id.choice_2})
    public void onRadioButtonCheckChanged(CompoundButton button, boolean checked) {
        if(checked) {
            switch (button.getId()) {
                case R.id.choice_1:
                    //mDeliveryAddress.setText(null);
                    //space.setVisibility(View.VISIBLE);
                    mDeliveryAddress.setVisibility(View.GONE);
                    break;
                case R.id.choice_2:
                    //space.setVisibility(View.GONE);
                    mDeliveryAddress.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @OnClick(R.id.checkout_button)
    public void goCheckout(View view) {
        // print keys using getKey() method
        for (Map.Entry<String, Integer> order :
                orderList.entrySet()) {
            System.out.println("Food Name: " + order.getKey());
            foodName.add(order.getKey());
            System.out.println("Amount: " + order.getValue());
            foodAmount.add(order.getValue());
        }

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(ShoppingCartActivity.KEY_USER_ID, customerId);
        intent.putExtra(ShoppingCartActivity.KEY_RESTAURANT_ID, restaurantId);
        Bundle b = new Bundle();
        if (choice_2.isChecked()) {
            b.putString(PaymentActivity.ADDRESS, mDeliveryAddress.getText().toString());
        }
        else {
            b.putString(PaymentActivity.ADDRESS, "");
        }
        b.putBoolean(PaymentActivity.DELIVERY, choice_2.isChecked());
        b.putDouble(PaymentActivity.PRICE, totalPrice[0]);
        b.putStringArrayList(PaymentActivity.FOOD_NAME, (ArrayList<String>) foodName);
        b.putIntegerArrayList(PaymentActivity.FOOD_AMOUNT, (ArrayList<Integer>) foodAmount);
        b.putString(PaymentActivity.NOTES, mNotes.getText().toString());
        intent.putExtra("bundle", b);
        Log.d("customerId", customerId);
        startActivity(intent);
    }
}