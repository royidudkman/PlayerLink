package com.example.playerlink.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerlink.R;
import com.example.playerlink.Result;
import com.example.playerlink.fragments.register_login_fragments.LoginFragment;
import com.example.playerlink.models.Message;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.AuthRepository;
import com.example.playerlink.repositories.AuthRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    private Context mContext;
    private List<Message> mMessageList;
    private AuthRepository mAuthRepository;
    private User currentUser = LoginFragment.GetCurrentUser();

    private ChatViewModel chatViewModel;

    private String chatId;
    public MessageListAdapter(Context context, List<Message> messageList, String chatId, ChatViewModel viewModel) {
        this.mContext = context;
        this.mMessageList = messageList;
        this.mAuthRepository = new AuthRepositoryFirebase();
        this.chatId = chatId;
        this.chatViewModel = viewModel;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            view = LayoutInflater.from(mContext).inflate(R.layout.my_message_card, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.other_message_card, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        if (getItemViewType(position) == VIEW_TYPE_MY_MESSAGE) {
            ((MyMessageViewHolder) holder).bind(message);
        } else {
            ((OtherMessageViewHolder) holder).bind(message);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    Message clickedMessage = mMessageList.get(clickedPosition);
                    if (currentUser != null && clickedMessage != null && currentUser.getUserId().equals(clickedMessage.getSenderId())) {
                        showDeleteConfirmationDialog(clickedMessage);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (currentUser != null) {
            Message message = mMessageList.get(position);
            if (message != null && message.getSenderId() != null && message.getSenderId().equals(currentUser.getUserId())) {
                return VIEW_TYPE_MY_MESSAGE;
            }
        }
        return VIEW_TYPE_OTHER_MESSAGE;
    }


    public void setMessageList(List<Message> messageList) {
        this.mMessageList = messageList;
        notifyDataSetChanged();
    }

    private static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView myMessageTextView;
        MyMessageViewHolder(View itemView) {
            super(itemView);
            myMessageTextView = itemView.findViewById(R.id.text_gchat_message_me);
        }

        void bind(Message message) {
            myMessageTextView.setText(message.getMessageText());
        }
    }

    private static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView otherMessageTextView;
        OtherMessageViewHolder(View itemView) {
            super(itemView);
            otherMessageTextView = itemView.findViewById(R.id.text_gchat_message_other);
        }

        void bind(Message message) {
            otherMessageTextView.setText(message.getMessageText());
        }
    }


    private void showDeleteConfirmationDialog(Message message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to delete this message?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call deleteMessage method on ChatViewModel
                        chatViewModel.deleteMessage(chatId, message);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
