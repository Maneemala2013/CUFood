package edu.cuhk.csci3310.cufood.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.R;
import edu.cuhk.csci3310.cufood.model.MenuCategory;
import edu.cuhk.csci3310.cufood.model.Restaurant;
import edu.cuhk.csci3310.cufood.util.RestaurantUtil;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class MenuCategoryAdapter extends FirestoreAdapter<MenuCategoryAdapter.ViewHolder> {
    public interface OnCategoryListener {

        void onCategorySelected(DocumentSnapshot menuCategory);
        void onCategoryDeleted(DocumentSnapshot menuCategory);
        void onOpenDialogEdited(DocumentSnapshot menuCategory, String categoryName, String categoryPhoto);
        void onAvailableStatus(DocumentSnapshot menuCategory, boolean status, String menuCategoryName);

    }

    private MenuCategoryAdapter.OnCategoryListener mListener;

    public MenuCategoryAdapter(Query query, OnCategoryListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public MenuCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MenuCategoryAdapter.ViewHolder(inflater.inflate(R.layout.item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(MenuCategoryAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.category_image)
        ImageView imageView;

        @BindView(R.id.category_name)
        TextView nameView;

        @BindView(R.id.remove_category)
        Button removeButton;

        @BindView(R.id.available_status)
        Switch availableSwitch;

        @BindView(R.id.edit_category)
        Button editButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final MenuCategoryAdapter.OnCategoryListener listener) {

            final MenuCategory category = snapshot.toObject(MenuCategory.class);
            Resources resources = itemView.getResources();

            // Load image
            String photo = category.getPhoto();
            if( photo.equals("")) {
                imageView.setImageResource(R.drawable.food);
            }
            else {
                Glide.with(imageView.getContext())
                        .load(photo)
                        .into(imageView);
            }
            nameView.setText(category.getCategoryName());
            availableSwitch.setChecked(category.getAvailableStatus());

            // Click listener
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (listener != null) {
//                        listener.onCategorySelected(snapshot);
//                    }
//                }
//            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCategorySelected(snapshot);
                    }
                }
            });

            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onCategoryDeleted(snapshot);
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onOpenDialogEdited(snapshot, category.getCategoryName(), category.getPhoto());
                    }
                }
            });

            availableSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean status = false;
                    if (availableSwitch.isChecked()) {
                        status = true;
                    }
                    if (listener != null) {
                        listener.onAvailableStatus(snapshot, status, nameView.getText().toString());
                    }
                }
            });
        }

    }
}
