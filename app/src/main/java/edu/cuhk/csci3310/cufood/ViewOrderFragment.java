package edu.cuhk.csci3310.cufood;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cuhk.csci3310.cufood.model.Order;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.cuhk.csci3310.cufood.model.Rating;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ViewOrderFragment extends DialogFragment {
    public static final String TAG = "ViewOrderFragment";

    private static final String ARG_OPTION = "option";
    public static final String ARG_ADDRESS = "address";
    public static final String ARG_TIME = "timestamp";
    public static final String ARG_USERID = "userID";
    public static final String ARG_FOOD_NAME = "foodName";
    public static final String ARG_FOOD_AMOUNT = "foodAmount";
    public static final String ARG_NOTES = "notes";
    public static final String ARG_PRICE = "price";
    public static final String ARG_RIDER = "rider";
    public static final String ARG_FINISHED = "finished";

    private String address;
    private boolean delivery; // false: pickup, true: delivery
    private ArrayList<String> foodName;
    private ArrayList<Integer> foodAmount;
    private String notes;
    private String riderInfo;
    private String timestamp;
    private double totalPrice;
    private String userId;
    private String finished;

    @BindView(R.id.view_order_option)
    TextView mOption;

    @BindView(R.id.view_order_address)
    TextView mAddress;

    @BindView(R.id.view_order_timestamp)
    TextView mTimestamp;

    @BindView(R.id.view_order_userId)
    TextView mUserId;

    @BindView(R.id.view_order_menus)
    TextView mMenus;

    @BindView(R.id.view_order_notes)
    TextView mNotes;

    @BindView(R.id.view_order_price)
    TextView mPrice;

    @BindView(R.id.view_order_rider)
    TextView mRider;

    @BindView(R.id.view_order_finished)
    TextView mFinishedTime;

    public ViewOrderFragment() {}

    public static ViewOrderFragment newInstance(Order order) {
        ViewOrderFragment fragment = new ViewOrderFragment();
        Bundle args = new Bundle();
        if (order != null) {
            args.putBoolean(ARG_OPTION, order.getDelivery());
            args.putString(ARG_ADDRESS, order.getAddress());
            args.putString(ARG_TIME, new SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm").format(order.getTimestamp()));
            args.putString(ARG_USERID, order.getUserId());
            args.putStringArrayList(ARG_FOOD_NAME, new ArrayList(order.getFoodName()));
            args.putIntegerArrayList(ARG_FOOD_AMOUNT, new ArrayList(order.getFoodAmount()));
            args.putString(ARG_NOTES, order.getNotes());
            args.putDouble(ARG_PRICE, order.getTotalPrice());
            args.putString(ARG_RIDER, order.getRiderInfo());
            Log.d(ViewOrderFragment.TAG, String.valueOf(order.getFinishedCookTime()));
            if (order.getFinishedCookTime() == null) {
                args.putString(ARG_FINISHED, "--- COOKING IN PROGRESS---");
            } else {
                args.putString(ARG_FINISHED, new SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm").format(order.getFinishedCookTime()));
            }
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            delivery = getArguments().getBoolean(ARG_OPTION);
            address = getArguments().getString(ARG_ADDRESS);
            timestamp = getArguments().getString(ARG_TIME);
            userId = getArguments().getString(ARG_USERID);
            foodName = getArguments().getStringArrayList(ARG_FOOD_NAME);
            foodAmount = getArguments().getIntegerArrayList(ARG_FOOD_AMOUNT);
            notes = getArguments().getString(ARG_NOTES);
            totalPrice = getArguments().getDouble(ARG_PRICE);
            riderInfo = getArguments().getString(ARG_RIDER);
            finished = getArguments().getString(ARG_FINISHED);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_view_order, container, false);
        ButterKnife.bind(this, v);
        if (!delivery) {
            mOption.setText("Pickup");
        }
        else {
            mAddress.setText(String.format("Delivery Address: %s", address));
            mRider.setText(String.format("Rider Information: %s", riderInfo));
        }
        mTimestamp.setText(String.format("Order Time: %s", timestamp));
        mUserId.setText(String.format("Customer Id: %s", userId));
        mNotes.setText(String.format("Notes: %s", notes));
        mPrice.setText(String.format("Total Price: %s", totalPrice));

        StringBuilder orderDetails = new StringBuilder();
        for (int i = 0; i < foodName.size(); i++) {
            orderDetails.append("- ");
            orderDetails.append(foodName.get(i));
            if (i < foodName.size() - 1)
                orderDetails.append(String.format(" (%s)\n", foodAmount.get(i)));
            else
                orderDetails.append(String.format(" (%s)", foodAmount.get(i)));
        }
        mMenus.setText(orderDetails);
        mFinishedTime.setText(String.format("Finished-Cooking Time: %s", finished));
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @OnClick(R.id.view_order_close)
    public void onClose(View view) {
        dismiss();
    }
}
