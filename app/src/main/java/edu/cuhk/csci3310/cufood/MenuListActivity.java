package edu.cuhk.csci3310.cufood;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.Transaction;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.cuhk.csci3310.cufood.adapter.MenuAdapter;
import edu.cuhk.csci3310.cufood.adapter.RatingAdapter;
import edu.cuhk.csci3310.cufood.model.Menu;
import edu.cuhk.csci3310.cufood.model.MenuCategory;
import edu.cuhk.csci3310.cufood.model.Restaurant;
import edu.cuhk.csci3310.cufood.util.RestaurantUtil;

public class MenuListActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, edu.cuhk.csci3310.cufood.MenuDialogFragment.MenuListener, MenuAdapter.OnMenuListener {

    public static final String KEY_RESTAURANT_ID = "key_restaurant_id";
    public static final String KEY_CATEGORY_ID = "key_category_id";
    private static final String TAG = "MenuListActivity";

    @BindView(R.id.menu_recyclerview)
    RecyclerView mAvailableRecycler;

    @BindView(R.id.menu_recyclerview_unavailable)
    RecyclerView mUnavailableRecycler;

    @BindView(R.id.view_empty_menu)
    ViewGroup mEmptyView;

    private MenuDialogFragment mMenuDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mRestaurantRef;
    private ListenerRegistration mRestaurantRegistration;

    private MenuAdapter mAvailableMenuAdapter;
    private MenuAdapter mUnavailableMenuAdapter;

    private String restaurantId;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        ButterKnife.bind(this);

        // Get restaurant ID from extras
        restaurantId = getIntent().getExtras().getString(KEY_RESTAURANT_ID);
        categoryId = getIntent().getExtras().getString(KEY_CATEGORY_ID);
        //Log.d(TAG, "categoryId"+categoryId);

        if (restaurantId == null | categoryId == null ) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RESTAURANT_ID + " or "+ KEY_CATEGORY_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mRestaurantRef = mFirestore.collection("restaurants").document(restaurantId);

        // Get available menus in the selected category
        Query availableMenuQuery = mRestaurantRef
                .collection("menu")
                .whereEqualTo("category", categoryId)
                .whereEqualTo("availableStatus", true);

        // Get available menus in the selected category
        Query unavailableMenuQuery = mRestaurantRef
                .collection("menu")
                .whereEqualTo("category", categoryId)
                .whereEqualTo("availableStatus", false);

        // RecyclerView
        mAvailableMenuAdapter = new MenuAdapter(availableMenuQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mAvailableRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mAvailableRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        // RecyclerView
        mUnavailableMenuAdapter = new MenuAdapter(unavailableMenuQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mUnavailableRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mUnavailableRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mAvailableRecycler.setLayoutManager(new LinearLayoutManager(this));
        mAvailableRecycler.setAdapter(mAvailableMenuAdapter);

        mUnavailableRecycler.setLayoutManager(new LinearLayoutManager(this));
        mUnavailableRecycler.setAdapter(mUnavailableMenuAdapter);

//        mRatingDialog = new RatingDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mAvailableMenuAdapter.startListening();
        mUnavailableMenuAdapter.startListening();
        mRestaurantRegistration = mRestaurantRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mAvailableMenuAdapter.stopListening();
        mUnavailableMenuAdapter.stopListening();

        if (mRestaurantRegistration != null) {
            mRestaurantRegistration.remove();
            mRestaurantRegistration = null;
        }
    }

    private Task<Void> addMenu(final DocumentReference restaurantRef, final Menu menu) {
        // Create a reference for new rating, for use inside the transaction
        final DocumentReference menuRef = restaurantRef.collection("menu")
                .document();
        Log.d(TAG, "addMenu is called");
        // In a transaction, add the new category and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {
                Restaurant restaurant = transaction.get(restaurantRef)
                        .toObject(Restaurant.class);

                // Commit to Firestore
                transaction.set(restaurantRef, restaurant);
                transaction.set(menuRef, menu);
                return null;
            }
        });
    }

    private Task<Void> updateMenu(final DocumentReference restaurantRef, final Menu menu, String menuId) {
        // Update
        restaurantRef.collection("menu")
                .document(menuId).update("menuName", menu.getMenuName(), "price", menu.getPrice(), "photo", menu.getPhoto(), "description", menu.getDescription(), "availableStatus", menu.getAvailableStatus(), "category", menu.getCategory()).addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Menu information updated");
                        hideKeyboard();
                        mAvailableRecycler.smoothScrollToPosition(0);
                        Toast.makeText(MenuListActivity.this, "The menu is updated.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Update menu information failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to update category",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        return null;
    }

    private Task<Void> deleteMenu(final DocumentReference restaurantRef, String menuId) {
        // Create a reference for new rating, for use inside the transaction
        restaurantRef.collection("menu")
                .document(menuId).delete().addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Menu deleted");
                        Toast.makeText(MenuListActivity.this, "The menu is deleted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Delete menu failed", e);

                        // Show failure message and hide keyboard
                        Snackbar.make(findViewById(android.R.id.content), "Failed to delete category",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        return null;
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }
    }

    @OnClick(R.id.fab_add_menu_dialog)
    public void onAddMenuClicked(View view) {
        mMenuDialog = MenuDialogFragment.newInstance("add", null, null, categoryId);
        mMenuDialog.show(getSupportFragmentManager(), MenuDialogFragment.TAG);
    }

    @Override
    public void onMenuAdded(Menu menu) {
        addMenu(mRestaurantRef, menu)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Menu added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mAvailableRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add menu failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add menu",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
        Log.d(TAG, "onMenuAdded is called");
    }

    @Override
    public void onMenuEdited(Menu menu, String menuId) {
        updateMenu(mRestaurantRef, menu, menuId);
    }

    @Override
    public void onMenuDelete(String menuId) {
        deleteMenu(mRestaurantRef, menuId);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onOpenDialogEdited(DocumentSnapshot menu, Menu menuToBeEdited) {
        mMenuDialog = MenuDialogFragment.newInstance("edit", menuToBeEdited, menu.getId(), categoryId);
        mMenuDialog.show(getSupportFragmentManager(), MenuDialogFragment.TAG);
    }
}