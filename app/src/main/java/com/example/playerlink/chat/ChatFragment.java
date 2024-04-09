package com.example.playerlink.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.playerlink.MainActivity;
import com.example.playerlink.databinding.FragmentChatBinding;
import com.example.playerlink.models.Message;
import com.example.playerlink.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private MessageListAdapter messageListAdapter;
    private List<Message> messageList;
    private ChatViewModel chatViewModel;

    private User currentUser;
    private User otherUser;
    private String chatId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);

        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable("currentUser");
            otherUser = (User) getArguments().getSerializable("otherUser");
            chatId = getArguments().getString("chatId");
            chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
            chatViewModel.initializeChat(chatId);

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        binding.usernameTitle.setText(otherUser.getUserName());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        initRecyclerView();
        observeViewModel(chatId);


        binding.buttonChatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void initRecyclerView() {
        messageList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(requireContext(), messageList);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(messageListAdapter);
    }

    private void observeViewModel(String chatId) {
        chatViewModel.readMessages(chatId);
        chatViewModel.getMessagesLiveData().observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                messageList.clear();
                messageList.addAll(messages);
                messageListAdapter.notifyDataSetChanged();
                binding.recycler.scrollToPosition(messageListAdapter.getItemCount() - 1);
            }
        });

    }

    private void sendMessage() {
        String messageText = binding.editChatMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {

            Message message = new Message();
            message.setMessageText(messageText);
            message.setSenderId(currentUser.getUserId());
            message.setTimestamp(System.currentTimeMillis());

            chatViewModel.sendMessage(chatId, message);

            binding.editChatMessage.getText().clear();
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
