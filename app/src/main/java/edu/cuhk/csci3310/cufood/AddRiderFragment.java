package edu.cuhk.csci3310.cufood;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cuhk.csci3310.cufood.model.MenuCategory;
import edu.cuhk.csci3310.cufood.model.Order;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddRiderFragment extends DialogFragment {
    public static final String TAG = "AddRiderFragment";

    private static final String ARG_OPTION = "option";
    private static final String ARG_ID = "id";
    private static final String ARG_CUSTOMER_ID = "cust_id";
    private static final String ARG_CUSTOMER_ORDER_ID = "cust_order_id";
    private boolean delivery; // false: pickup, true: delivery
    private String id;
    private String custId;
    private String custOrderId;

    @BindView(R.id.rider_title)
    TextView riderTitle;

    @BindView(R.id.rider_form)
    TextView riderForm;

    interface FinishOrderListener {
        void onFinishRiderOrder(String orderId, Date finishedTime, String riderDetails, boolean delivery, String customerId, String customerOrderId);
    }

    public AddRiderFragment() {
    }

    public static AddRiderFragment newInstance(Order order, String orderId, String customerId, String customerOrderId) {
        AddRiderFragment fragment = new AddRiderFragment();
        Bundle args = new Bundle();
        if (order != null) {
            args.putBoolean(ARG_OPTION, order.getDelivery());
            args.putString(ARG_ID, orderId);
            args.putString(ARG_CUSTOMER_ID, customerId);
            args.putString(ARG_CUSTOMER_ORDER_ID, customerOrderId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private AddRiderFragment.FinishOrderListener mFinishOrderListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            delivery = getArguments().getBoolean(ARG_OPTION);
            id = getArguments().getString(ARG_ID);
            custId = getArguments().getString(ARG_CUSTOMER_ID);
            custOrderId = getArguments().getString(ARG_CUSTOMER_ORDER_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_rider_order, container, false);
        ButterKnife.bind(this, v);
        if (!delivery) {
            riderTitle.setVisibility(View.GONE);
            riderForm.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddRiderFragment.FinishOrderListener) {
            mFinishOrderListener = (AddRiderFragment.FinishOrderListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @OnClick(R.id.order_form_cancel)
    public void onClose(View view) {
        dismiss();
    }

    @OnClick({R.id.order_form_finish})
    public void onSubmitClicked(View view) {
        if (mFinishOrderListener != null) {
            if (delivery) {
                mFinishOrderListener.onFinishRiderOrder(id, new Date(), riderForm.getText().toString(), delivery, custId, custOrderId);
            } else {
                mFinishOrderListener.onFinishRiderOrder(id, new Date(), "", delivery, custId, custOrderId);
            }
        }
        dismiss();
    }
}
