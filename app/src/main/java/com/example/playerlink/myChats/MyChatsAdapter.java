package com.example.playerlink.myChats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerlink.R;
import com.example.playerlink.Result;
import com.example.playerlink.fragments.register_login_fragments.LoginFragment;
import com.example.playerlink.models.Chat;
import com.example.playerlink.models.Message;
import com.example.playerlink.models.User;
import com.example.playerlink.repositories.ChatRepository;
import com.example.playerlink.repositories.ChatRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;

import java.util.List;

public class MyChatsAdapter extends RecyclerView.Adapter<MyChatsAdapter.MyViewHolder> {

    private Context mContext;
    private List<User> mUsersList;
    private User currentUser;
    private OnUserClickListener mListener;

    public interface OnUserClickListener {
        void onUserClick(User currentUser, User otherUser);
    }

    public MyChatsAdapter(Context context, List<User> usersList, User currentUser, OnUserClickListener listener) {
        this.mContext = context;
        this.mUsersList = usersList;
        this.currentUser = currentUser;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = mUsersList.get(position);
        holder.bind(user);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onUserClick(currentUser, user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView lastMessage;
        ImageView userImage;
        ChatRepository chatRepository;
        User currentUser = LoginFragment.GetCurrentUser();

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.username_title);
            lastMessage = itemView.findViewById(R.id.last_message_text);
            userImage = itemView.findViewById(R.id.user_image);
            chatRepository = new ChatRepositoryFirebase();
        }

        public void bind(User user) {
            userNameTextView.setText(user.getUserName());

            if(user.getUserImage() == null) {
                userImage.setImageResource(R.mipmap.ic_launcher);
            } else {
                userImage.setImageBitmap(user.getUserImage());
            }

            String chatId = makeChatId(currentUser.getUserId(),user.getUserId());
            chatRepository.getLastMessage(chatId, new RepositoryCallback<Message>() {
                @Override
                public void onComplete(Result<Message> result) {
                    if (result instanceof Result.Success) {
                        Message message = ((Result.Success<Message>) result).data;
                        if (message != null) {
                            lastMessage.setText(message.getMessageText());
                        } else {
                            lastMessage.setText(R.string.no_messages_yet);
                        }
                    } else {

                    }
                }
            });

        }
    }

    public void setUserList(List<User> userList) {
        mUsersList = userList;
        notifyDataSetChanged();
    }


    private static String makeChatId(String userId1, String userId2) {
        String chatId;

        if (userId1.compareTo(userId2) < 0) {
            chatId = userId1 + "_with_" + userId2;
        } else {
            chatId = userId2 + "_with_" + userId1;
        }
        return chatId;
    }
}
