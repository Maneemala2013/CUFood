package edu.cuhk.csci3310.cufood;

import static com.google.android.gms.common.util.CollectionUtils.mapOf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.cuhk.csci3310.cufood.adapter.MenuCategoryAdapter;
import edu.cuhk.csci3310.cufood.model.MenuCategory;
import edu.cuhk.csci3310.cufood.model.Restaurant;

public class MenuCategoryActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, edu.cuhk.csci3310.cufood.CategoryDialogFragment.MenuCategoryListener, MenuCategoryAdapter.OnCategoryListener {

    private static final String TAG = "MenuCategory";

    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    public static final String KEY_CATEGORY_ID = "key_category_id";
    private String name = "", category = "", location = "", photo = "";
    private double rating = 0;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.category_recyclerview)
    RecyclerView mCategoryRecycler;

    @BindView(R.id.view_empty_category)
    ViewGroup mEmptyView;

    private CategoryDialogFragment mCategoryDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mRestaurantRef;
    private ListenerRegistration mRestaurantRegistration;

    private MenuCategoryAdapter mCategoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_category);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        // Get restaurant ID from extras
        String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        Log.d(TAG, restaurantId);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mRestaurantRef = mFirestore.collection("restaurants").document(restaurantId);

        //get the field names for canteen setting
        getFieldNames();

        // Get categories
        Query categoryQuery = mRestaurantRef
                .collection("categories")
                .orderBy("categoryName", Query.Direction.ASCENDING);

        // RecyclerView
        mCategoryAdapter = new MenuCategoryAdapter(categoryQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mCategoryRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mCategoryRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mCategoryRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCategoryRecycler.setAdapter(mCategoryAdapter);

        //mCategoryDialog = new CategoryDialogFragment();
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
                break;
            case R.id.order_management:
                intent = new Intent(this, OrderActivity.class);
                intent.putExtra(RestaurantDetailActivity.KEY_RESTAURANT_ID, restaurantId);
                startActivity(intent);
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
                intent = new Intent(MenuCategoryActivity.this, homepage.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        mCategoryAdapter.startListening();
        mRestaurantRegistration = mRestaurantRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mCategoryAdapter.stopListening();

        if (mRestaurantRegistration != null) {
            mRestaurantRegistration.remove();
            mRestaurantRegistration = null;
        }
    }

    private Task<Void> addCategory(final DocumentReference restaurantRef, final MenuCategory menuCategory) {
        // TODO(developer): Implement
        // Create a reference for new rating, for use inside the transaction
        final DocumentReference categoryRef = restaurantRef.collection("categories")
                .document();
        // In a transaction, add the new category and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {
                Restaurant restaurant = transaction.get(restaurantRef)
                        .toObject(Restaurant.class);

                // Commit to Firestore
                transaction.set(restaurantRef, restaurant);
                transaction.set(categoryRef, menuCategory);
                return null;
            }
        });
    }

    private Task<Void> deleteCategory(final DocumentReference restaurantRef, final DocumentSnapshot menuCategory) {
        // Create a reference for new rating, for use inside the transaction
        restaurantRef.collection("categories")
                .document(menuCategory.getId()).delete().addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Category deleted");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Delete category failed", e);

                        // Show failure message and hide keyboard
                        Snackbar.make(findViewById(android.R.id.content), "Failed to delete category",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        return null;
    }

    private Task<Void> updateCategoryStatus(final DocumentReference restaurantRef, final DocumentSnapshot menuCategory, final boolean status, final String menuCategoryName) {
        // Update menu category status
        restaurantRef.collection("categories")
                .document(menuCategory.getId()).update("availableStatus", status).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Category status updated");
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Update category status failed", e);

                        // Show failure message and hide keyboard
                        Snackbar.make(findViewById(android.R.id.content), "Failed to delete category",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        // Query the collection and loop through each document to set available status of related menus
        restaurantRef.collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Access the price field of each document and add it to the total price
                        String category = document.getString("category");
                        Log.d(TAG, menuCategory.getId());
                        if (category.equals(menuCategory.getId())) {
                            restaurantRef.collection("menu").document(document.getId()).update("availableStatus", status);
                        }
                    }
                } else {
                    // Handle errors
                    System.out.println("Error updataing menu status: " + task.getException().getMessage());
                }
            }
        });
        return null;
    }

    private Task<Void> updateCategory(final DocumentReference restaurantRef, final MenuCategory category, String categoryId) {
        // Update
        restaurantRef.collection("categories")
                .document(categoryId).update("categoryName", category.getCategoryName(), "photo", category.getPhoto()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Category information updated");
                        hideKeyboard();
                        mCategoryRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Update category information failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to update category",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        return null;
    }

    /**
     * Listener for the Restaurant document ({@link #mRestaurantRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }
    }

    @OnClick(R.id.fab_add_category_dialog)
    public void onAddCategoryClicked(View view) {
        mCategoryDialog = CategoryDialogFragment.newInstance("add", null, null, null);
        mCategoryDialog.show(getSupportFragmentManager(), CategoryDialogFragment.TAG);
    }

    @Override
    public void onCategory(MenuCategory menuCategory) {
        // In a transaction, add the new category
        addCategory(mRestaurantRef, menuCategory)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Category added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mCategoryRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add category failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add category",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onCategoryEdited(MenuCategory menuCategory, String categoryId) {
        Toast.makeText(this, "The information of the selected category is updated.", Toast.LENGTH_SHORT).show();
        updateCategory(mRestaurantRef, menuCategory, categoryId);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onCategorySelected(DocumentSnapshot menuCategory) {
        // Go to the details page for the selected category
//        Toast.makeText(this, "You have selected ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MenuListActivity.class);
        String restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        intent.putExtra(MenuListActivity.KEY_RESTAURANT_ID, restaurantId);
        intent.putExtra(MenuListActivity.KEY_CATEGORY_ID, menuCategory.getId());
        startActivity(intent);
    }

    @Override
    public void onCategoryDeleted(final DocumentSnapshot menuCategory) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Do you want to delete this menu category?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCategory(mRestaurantRef, menuCategory);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onOpenDialogEdited(final DocumentSnapshot menuCategory, String categoryName, String categoryPhoto) {
        mCategoryDialog = CategoryDialogFragment.newInstance("edit", menuCategory.getId(), categoryName, categoryPhoto);
        mCategoryDialog.show(getSupportFragmentManager(), CategoryDialogFragment.TAG);
    }

    @Override
    public void onAvailableStatus(DocumentSnapshot menuCategory, boolean status, String menuCategoryName) {
        Toast.makeText(this, "The status of the selected category is updated.", Toast.LENGTH_SHORT).show();
        updateCategoryStatus(mRestaurantRef, menuCategory, status, menuCategoryName);
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