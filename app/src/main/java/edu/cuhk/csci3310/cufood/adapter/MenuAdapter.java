package edu.cuhk.csci3310.cufood.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.R;
import edu.cuhk.csci3310.cufood.model.Menu;
import edu.cuhk.csci3310.cufood.model.MenuCategory;
import edu.cuhk.csci3310.cufood.model.Rating;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class MenuAdapter extends FirestoreAdapter<MenuAdapter.ViewHolder> {

    public interface OnMenuListener {

        void onOpenDialogEdited(DocumentSnapshot menu, Menu menuToBeEdited);

    }

    private MenuAdapter.OnMenuListener mListener;

    public MenuAdapter(Query query, OnMenuListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MenuAdapter.ViewHolder(inflater.inflate(R.layout.item_menu, parent, false));
    }

    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.menu_image)
        ImageView mMenuImage;

        @BindView(R.id.menu_name)
        TextView mMenuName;

        @BindView(R.id.menu_price)
        TextView mMenuPrice;

        @BindView(R.id.menu_description)
        TextView mMenuDescription;

        @BindView(R.id.menu_edit)
        Button mEditButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final MenuAdapter.OnMenuListener listener) {
            final Menu menu = snapshot.toObject(Menu.class);
            if (menu.getPhoto() != null) {
                Glide.with(mMenuImage.getContext())
                        .load(menu.getPhoto())
                        .into(mMenuImage);
            }
            mMenuName.setText(menu.getMenuName());
            mMenuPrice.setText(String.valueOf(menu.getPrice()));
            mMenuDescription.setText(menu.getDescription());
            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onOpenDialogEdited(snapshot, menu);
                    }
                }
            });
        }
    }

}
