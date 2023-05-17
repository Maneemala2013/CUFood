package edu.cuhk.csci3310.cufood.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.R;
import edu.cuhk.csci3310.cufood.model.Menu;

public class MenuCustomerAdapter extends FirestoreAdapter<MenuCustomerAdapter.ViewHolder>{

    private CollectionReference collectionReference;
    private static final String TAG = "MenuCustomerAdapter";

    public MenuCustomerAdapter(Query query, FirebaseFirestore db, String customerId){
        super(query);
        this.collectionReference = db.collection("customers").document(customerId).collection("shopping_cart");
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_menu_customer, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
        final Menu menu = getSnapshot(position).toObject(Menu.class);
        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a data object that includes the name, photo, and price attributes of the recycler item

                Map<String, Object> data = new HashMap<>();
                data.put("menuName", menu.getMenuName());
                data.put("photo", menu.getPhoto());
                data.put("price", menu.getPrice());

                // Use the Firebase API to add the data object to the collection
                collectionReference.add(data)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Recycler item added to Firebase collection");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding recycler item to Firebase collection", e);
                            }
                        });
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.food_image)
        ImageView mFoodImage;

        @BindView(R.id.food_name)
        TextView mFoodName;

        @BindView(R.id.food_description)
        TextView mFoodDescription;

        @BindView(R.id.food_price)
        TextView mFoodPrice;

        @BindView(R.id.add_to_cart)
        TextView mAddButton;;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot) {
            Menu menu = snapshot.toObject(Menu.class);
            Resources resources = itemView.getResources();

//            Glide.with(mFoodImage.getContext())
//                    .load(menu.getPhoto())
//                    .into(mFoodImage);

            // Load image
            String photo = menu.getPhoto();
            if( photo.equals("")) {
                mFoodImage.setImageResource(R.drawable.food);
            }
            else {
                Glide.with(mFoodImage.getContext())
                        .load(photo)
                        .into(mFoodImage);
            }
            mFoodName.setText(menu.getMenuName());
            mFoodPrice.setText("HK$ " + Double.toString(menu.getPrice()));
            mFoodDescription.setText(menu.getDescription());

        }
    }

}
