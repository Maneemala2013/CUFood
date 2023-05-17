package edu.cuhk.csci3310.cufood.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.cuhk.csci3310.cufood.R;
import edu.cuhk.csci3310.cufood.model.Order;

public class OrderAdapter extends FirestoreAdapter<OrderAdapter.ViewHolder> {

    public interface OnOrderListener {
        void onViewOrder(DocumentSnapshot menuCategory, Order order);
        void onFinishOrder(DocumentSnapshot menuCategory, Order order);
        void onDeleteOrder(DocumentSnapshot menuCategory);
    }

    private OrderAdapter.OnOrderListener mListener;
    public OrderAdapter(Query query, OnOrderListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new OrderAdapter.ViewHolder(inflater.inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.order_card)
        View mCard;

        @BindView(R.id.order_timestamp)
        TextView mTimestamp;

//        @BindView(R.id.order_userId)
//        TextView mUserId;

        @BindView(R.id.order_option)
        TextView mOption;

        @BindView(R.id.order_address)
        TextView mAddress;

        @BindView(R.id.order_details)
        TextView mOrderDetails;

        @BindView(R.id.order_view)
        TextView mViewButton;

        @BindView(R.id.order_finish)
        Button mFinishButton;

        @BindView(R.id.order_delete)
        Button mDeleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OrderAdapter.OnOrderListener listener) {
            final Order order = snapshot.toObject(Order.class);
            String timestamp = new SimpleDateFormat("dd-MMM-yyyy 'at' HH:mm").format(order.getTimestamp());
            mTimestamp.setText(timestamp);
            Log.d("PendingOrderAdapter", String.valueOf(order.getTimestamp()));
//            mUserId.setText(order.getUserId());
            if (!order.getDelivery()) {
                mOption.setText("Pickup");
            }
            mAddress.setText(order.getAddress());
            List<String> foodName = order.getFoodName();
            List<Integer> foodAmount = order.getFoodAmount();
            StringBuilder orderDetails = new StringBuilder();
            for (int i = 0; i < foodName.size(); i++) {
                orderDetails.append(foodName.get(i));
                if (i < foodName.size() - 1)
                    orderDetails.append(String.format(" (%s), ", foodAmount.get(i)));
                else
                    orderDetails.append(String.format(" (%s)", foodAmount.get(i)));
            }
            mOrderDetails.setText(orderDetails);
            mViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onViewOrder(snapshot, order);
                    }
                }
            });


            if (order.getFinishedStatus()) {
                mFinishButton.setVisibility(View.GONE);
                mDeleteButton.setVisibility(View.VISIBLE);
                mDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onDeleteOrder(snapshot);
                        }
                    }
                });
            }
            else {
                mFinishButton.setVisibility(View.VISIBLE);
                mDeleteButton.setVisibility(View.GONE);
                /* Change the card color to yellow and red after the order time has already passed for 5 and 10 minutes respectively */
                Date currentTime = new Date();
                Calendar c5 = Calendar.getInstance();
                c5.setTime(currentTime);
                Calendar c10 = Calendar.getInstance();
                c10.setTime(currentTime);

                c5.add(Calendar.MINUTE, -5);
                c10.add(Calendar.MINUTE, -10);

                Date currentTimeFive = c5.getTime();
                Date currentTimeTen = c10.getTime();

                Date orderTime = order.getTimestamp();
                if (orderTime.compareTo(currentTimeTen) < 0) {
                    mCard.setBackgroundResource(R.color.red);
                } else if (orderTime.compareTo(currentTimeFive) < 0) {
                    mCard.setBackgroundResource(R.color.yellow);
                } else {
                    mCard.setBackgroundResource(R.color.green);
                }
                mFinishButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            listener.onFinishOrder(snapshot, order);
                        }
                    }
                });
            }
        }
    }
}
