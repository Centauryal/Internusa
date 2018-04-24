package com.supersoft.internusa.ui.chat;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.CircleTransform;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.FlipAnimator;
import com.supersoft.internusa.model.ChatGroupModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by itclub21 on 12/10/2017.
 */

class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MyViewHolder> {
    private Context mContext;
    private List<ChatGroupModel> messages;
    private MessageAdapterListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView from, subject, message, iconText, timestamp, txtBadge;
        public ImageView imgProfile;
        public LinearLayout messageContainer;
        public RelativeLayout iconContainer, iconBack, iconFront, rlLayout;

        public MyViewHolder(View view) {
            super(view);
            from = view.findViewById(R.id.from);
            subject = view.findViewById(R.id.txt_primary);
            message = view.findViewById(R.id.txt_secondary);
            iconText = view.findViewById(R.id.icon_text);
            timestamp = view.findViewById(R.id.timestamp);
            iconBack = view.findViewById(R.id.icon_back);
            rlLayout = view.findViewById(R.id.rl_layout);
            iconFront = view.findViewById(R.id.icon_front);
            txtBadge = view.findViewById(R.id.txtBadge);
            imgProfile = view.findViewById(R.id.icon_profile);
            messageContainer = view.findViewById(R.id.message_container);
            iconContainer = view.findViewById(R.id.icon_container);
            view.setOnLongClickListener(this);
            //ButterKnife.bind(this, view);

        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }


    public ChatMessageAdapter(Context mContext, List<ChatGroupModel> messages, MessageAdapterListener listener) {
        this.mContext = mContext;
        this.messages = messages;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_group_listview_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        ChatGroupModel message = messages.get(position);

        if(message.IMPORTANT)
            holder.rlLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bg_list_row_priority));
        else
            holder.rlLayout.setBackground(mContext.getResources().getDrawable(R.drawable.bg_list_row));

        holder.from.setText((message.CREATORNAME == null) ? "" : message.CREATORNAME);
        holder.subject.setText((message.TOPIK == null) ? "" : message.TOPIK);

        try{
            if(message.LASTMESSAGE.equals(""))
            {
                holder.message.setVisibility(View.GONE);
            }
        }catch (NullPointerException e)
        {
            holder.message.setVisibility(View.GONE);
        }
        holder.message.setText((message.LASTMESSAGE == null) ? "" : message.NAMAPENGIRIMTERAKHIR + " : " + message.LASTMESSAGE);
        holder.timestamp.setText((message.LAST_UPDATE == null) ? "" : Constant.parseDate(message.LAST_UPDATE));
        boolean ischecked = (message.ISCHECKED == null) ? false : message.ISCHECKED;
        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // change the font style depending on message read status
        applyReadStatus(holder, message);

        // handle message star
        applyImportant(holder, message);

        // handle icon animation
        applyIconAnimation(holder, position);

        // display profile image
        applyProfilePicture(holder, message);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.messageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });

        holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
    }

    private void applyProfilePicture(MyViewHolder holder, ChatGroupModel message) {
        if (!TextUtils.isEmpty(message.AVATAR)) {
            Glide.with(mContext).load(message.AVATAR)
                    .thumbnail(0.5f)
                    .crossFade()
                    .transform(new CircleTransform(mContext))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgProfile);
            holder.imgProfile.setColorFilter(null);

        } else {
            holder.imgProfile.setImageResource(R.drawable.bg_circle);
            holder.imgProfile.setColorFilter(message.COLOR);

        }
    }

    private void applyIconAnimation(MyViewHolder holder, int position) {
        if (selectedItems.get(position, false)) {
            holder.iconFront.setVisibility(View.GONE);
            resetIconYAxis(holder.iconBack);
            holder.iconBack.setVisibility(View.VISIBLE);
            holder.iconBack.setAlpha(1);
            if (currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, true);
                resetCurrentIndex();
            }
        } else {
            holder.iconBack.setVisibility(View.GONE);
            resetIconYAxis(holder.iconFront);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
                FlipAnimator.flipView(mContext, holder.iconBack, holder.iconFront, false);
                resetCurrentIndex();
            }
        }
    }

    private void applyImportant(MyViewHolder holder, ChatGroupModel message) {
        if (message.IMPORTANT) {
            //holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_black_24dp));
            //holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_selected));
        } else {
            //holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_star_border_black_24dp));
            //holder.iconImp.setColorFilter(ContextCompat.getColor(mContext, R.color.icon_tint_normal));
        }
    }

    // As the views will be reused, sometimes the icon appears as
    // flipped because older view is reused. Reset the Y-axis to 0
    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(messages.get(position).ID);
    }

    private void applyReadStatus(MyViewHolder holder, ChatGroupModel message) {
        if (message.ISUNREAD <= 0) {
            holder.timestamp.setTypeface(null, Typeface.NORMAL);
            //holder.subject.setTypeface(null, Typeface.NORMAL);
            holder.message.setTypeface(null, Typeface.NORMAL);
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.message.setTextColor(ContextCompat.getColor(mContext, R.color.message));
            holder.txtBadge.setVisibility(View.GONE);
        } else {
            holder.timestamp.setTypeface(null, Typeface.BOLD);
            //holder.subject.setTypeface(null, Typeface.BOLD);
            holder.message.setTypeface(null, Typeface.BOLD);
            holder.subject.setTextColor(ContextCompat.getColor(mContext, R.color.from));
            holder.message.setTextColor(ContextCompat.getColor(mContext, R.color.subject));
            holder.txtBadge.setVisibility(View.VISIBLE);

            holder.txtBadge.setText("" + message.ISUNREAD);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public void updateItem(ChatGroupModel model, int position)
    {
        ChatGroupModel item = messages.get(position);
        item = model;
        notifyDataSetChanged();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        reverseAllAnimations = true;
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        messages.remove(position);
        resetCurrentIndex();
    }

    public void removeDataWithDb(DBHelper db, int position)
    {
        ChatGroupModel chat = messages.get(position);
        db.deleteChatGroup(chat.IDBASE);
        removeData(position);
    }

    public void resetData(List<ChatGroupModel> messages)
    {
        this.messages.clear();
        this.messages = messages;
        notifyDataSetChanged();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

}
