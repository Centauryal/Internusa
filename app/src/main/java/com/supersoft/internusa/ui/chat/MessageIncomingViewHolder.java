package com.supersoft.internusa.ui.chat;

import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import com.supersoft.internusa.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by itclub21 on 12/7/2017.
 */

public class MessageIncomingViewHolder extends MessageViewHolder implements View.OnLongClickListener{
    @BindView(R.id.txtSender) public TextView txtSender;
    @BindView(R.id.txtMessage) public TextView txtMessage;
    @BindView(R.id.txtDate) public TextView txtDate;
    private MessageAdapterListener listener;
    public MessageIncomingViewHolder(View itemView) {
        super(itemView);
        itemView.setOnLongClickListener(this);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public boolean onLongClick(View view) {
        listener.onRowLongClicked(getAdapterPosition());
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        return true;
    }
}
