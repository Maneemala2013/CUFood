package edu.cuhk.csci3310.cufood;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

import edu.cuhk.csci3310.cufood.adapter.ShoppingCartAdapter;
import edu.cuhk.csci3310.cufood.model.Order;
import edu.cuhk.csci3310.cufood.model.Restaurant;

public class PaymentActivity extends AppCompatActivity {

    public static final String TAG = "PaymentActivity";
    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    public static final String KEY_USER_ID = "key_user_id";
    public static final String ADDRESS = "address";
    public static final String DELIVERY = "delivery";
    public static final String FOOD_AMOUNT = "foodAmount";
    public static final String FOOD_NAME = "foodName";
    public static final String PRICE = "price";
    public static final String NOTES = "notes";

    private FirebaseFirestore mFirestore;
    private DocumentReference mRestaurantRef;
    private CollectionReference mShoppingRef;
    private Query mQuery;
    private DocumentReference mCustomerRef;
    private String customerId;
    private String restaurantId;

    private String address;
    private boolean delivery;
    private List<String> foodName;
    private List<Integer> foodAmount;
    private double totalPrice;
    private String notes;

    private String customerOrderId = null;

    Button  cancel, confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        cancel = (Button) findViewById(R.id.cancel);
        confirm = (Button) findViewById(R.id.confirm);
        RadioGroup paymentMethodRadioGroup = findViewById(R.id.payment_method);

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

        // Get bundle
        Bundle b = getIntent().getExtras().getBundle("bundle");
        address = b.getString(ADDRESS);
        delivery = b.getBoolean(DELIVERY);
        foodAmount = b.getIntegerArrayList(FOOD_AMOUNT);
        foodName = b.getStringArrayList(FOOD_NAME);
        totalPrice = b.getDouble(PRICE);
        notes = b.getString(NOTES);

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mRestaurantRef = mFirestore.collection("restaurants").document(restaurantId);

        // Get reference to the customer
        mCustomerRef = mFirestore.collection("customers").document(customerId);

        // Get reference to the shopping cart
        mShoppingRef = mFirestore.collection("customers").document(customerId).collection("shopping_cart");
        paymentMethodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.cash:
                        // adding intent after selecting cash option
                        // moving to order status activity
                        //Intent intent = new Intent(PaymentActivity.this, orderStatusActivity.class);
                        //startActivity(intent);
                        break;
                    case R.id.credit_card:
                        // adding intent after selecting credit card option
                        // moving to order status activity
                        //Intent intent = new Intent(PaymentActivity.this, orderStatusActivity.class);
                        //startActivity(intent);
                        break;
                    case R.id.octopus:
                        // adding intent after selecting octopus option
                        // moving to order status activity
                        //Intent intent = new Intent(PaymentActivity.this, orderStatusActivity.class);
                        //startActivity(intent);
                        break;
                    case R.id.PayMe:
                        // adding intent after selecting payme option
                        // moving to order status activity
                        //Intent intent = new Intent(PaymentActivity.this, orderStatusActivity.class);
                        //startActivity(intent);
                        break;
                    default:
                        Toast.makeText(PaymentActivity.this, "Please select an option!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        // handling the cancel button
        // intent to the previous activity
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, ShoppingCartActivity.class);
                intent.putExtra(PaymentActivity.KEY_USER_ID, customerId);
                startActivity(intent);
            }
        });
        // handling the confirm button
        // intent to next activity - order status
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: UN-COMMENT THIS CODE WHEN ACTIVITY ORDER IS CREATED
                //Intent intent = new Intent(PaymentActivity.this, orderStatusActivity.class);
                //startActivity(intent);

                // Save order history to the customer side
                final Order newOrder = new Order(address, delivery, null, false, foodName, foodAmount, notes, "", totalPrice, customerId, "");
                final CollectionReference customerRef = mCustomerRef.collection("orders");
                customerRef.add(newOrder)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                customerOrderId = documentReference.getId();
                                Log.d("PaymentActivity", customerOrderId);
                                Toast.makeText(PaymentActivity.this, "Your order status can be checked in Order Status", Toast.LENGTH_SHORT).show();
                                saveToRestaurant();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PaymentActivity.this, "Your order is not saved to the history", Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        });
    }
    public void saveToRestaurant(){
        // Submit the new order to restaurants
        final Order newOrderRestaurant = new Order(address, delivery, null, false, foodName, foodAmount, notes, "", totalPrice, customerId, customerOrderId);
        final CollectionReference restaurantRef = mRestaurantRef.collection("orders");
        restaurantRef.add(newOrderRestaurant)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(PaymentActivity.this, "Your order is submitted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PaymentActivity.this, CustomerOrderActivity.class);
//                        intent.putExtra(CustomerOrderActivity.KEY_ORDER_ID, customerOrderId);
                        intent.putExtra(CustomerOrderActivity.KEY_USER_ID, customerId);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PaymentActivity.this, "Your order submission is not successful!", Toast.LENGTH_SHORT).show();
                    }
                });

        // Clear the shopping cart
        mShoppingRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
}
