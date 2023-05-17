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

public class ShoppingCartAdapter extends FirestoreAdapter<ShoppingCartAdapter.ViewHolder>{

    private CollectionReference collectionReference;
    private OnDataChangedListener listener;
    private static final String TAG = "ShoppingCartAdapter";

    public ShoppingCartAdapter(Query query, FirebaseFirestore db, OnDataChangedListener listener, String customerId){
        super(query);
        this.collectionReference = db.collection("customers").document(customerId).collection("shopping_cart");
        this.listener = listener;
    }

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_shopping_cart, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position));
        final Menu menu = getSnapshot(position).toObject(Menu.class);
        final int pos = position;
        holder.mRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentSnapshot documentSnapshot = getSnapshot(pos);
                String documentId = documentSnapshot.getId();

                // Delete the document from the collection
                collectionReference.document(documentId).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.w(TAG, "Successfully deleted recycler item from Firebase collection");
                                onDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting recycler item from Firebase collection", e);
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

        @BindView(R.id.food_price)
        TextView mFoodPrice;

        @BindView(R.id.button_remove)
        TextView mRemoveButton;;

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

            mFoodName.setText(menu.getMenuName());
            mFoodPrice.setText("HK$ " + Double.toString(menu.getPrice()));

        }
    }

    private void onDataSetChanged() {
        if (listener != null) {
            listener.onDataChanged();
        }
    }

}
