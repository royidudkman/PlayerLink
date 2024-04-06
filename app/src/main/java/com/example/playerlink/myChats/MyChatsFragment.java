package com.example.playerlink.myChats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.all_users.MyChatsAdapter;
import com.example.playerlink.chat.ChatFragment;
import com.example.playerlink.databinding.FragmentMyChatsBinding;
import com.example.playerlink.models.User;

import java.util.ArrayList;
import java.util.List;

public class MyChatsFragment extends Fragment implements MyChatsAdapter.OnUserClickListener {

    private FragmentMyChatsBinding binding;
    private MyChatsViewModel viewModel;
    private MyChatsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMyChatsBinding.inflate(inflater, container, false);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.VISIBLE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MyChatsViewModel.class);

        viewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    // Initialize adapter with current user
                    adapter = new MyChatsAdapter(requireContext(), new ArrayList<>(), user, MyChatsFragment.this);
                    binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.recycler.setAdapter(adapter);
                } else {
                    // Handle the case where current user is null
                }
            }
        });

        viewModel.getChatsWithUsersLiveData().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                // Update the adapter with the list of users
                if (adapter != null) {
                    adapter.setUserList(users);
                }
            }
        });
    }


    private String makeChatId(String userId1, String userId2){
        String chatId;

        if (userId1.compareTo(userId2) < 0) {
            chatId = userId1 + "_with_" + userId2;
        } else {
            chatId = userId2 + "_with_" + userId1;
        }
        return chatId;
    }

    @Override
    public void onUserClick(User currentUser, User otherUser) {
        String chatId = makeChatId(currentUser.getUserId(), otherUser.getUserId());
        Bundle args = new Bundle();
        args.putSerializable("currentUser", currentUser);
        args.putSerializable("otherUser", otherUser);
        args.putString("chatId", chatId);

        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(args);

        NavHostFragment.findNavController(this).navigate(R.id.action_myChatsFragment_to_chatFragment,args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
