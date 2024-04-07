package com.example.playerlink.all_users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerlink.R;
import com.example.playerlink.models.User;

import java.util.List;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.UserViewHolder> {

    private Context mContext;
    private List<User> mUsersList;
    private User currentUser;
    private OnUserClickListener mListener;

    public interface OnUserClickListener {
        void onUserClick(User currentUser, User otherUser);
    }

    public AllUsersAdapter(Context context, List<User> usersList, User currentUser, OnUserClickListener listener) {
        this.mContext = context;
        this.mUsersList = usersList;
        this.currentUser = currentUser;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_with_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = mUsersList.get(position);
        holder.bind(user);

        holder.addFriendBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mListener.onUserClick(currentUser,user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        Button addFriendBtn;
        ImageView userImage;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.username_title);
            addFriendBtn = itemView.findViewById(R.id.start_chat_btn);
            userImage = itemView.findViewById(R.id.user_image);
        }

        public void bind(User user) {
            userNameTextView.setText(user.getUserName());
            if(user.getUserImage() == null) {
                userImage.setImageResource(R.mipmap.ic_launcher);
            } else {
                userImage.setImageBitmap(user.getUserImage());
            }
        }
    }

    public void setUserList(List<User> userList) {
        mUsersList = userList;
        notifyDataSetChanged();
    }
}
