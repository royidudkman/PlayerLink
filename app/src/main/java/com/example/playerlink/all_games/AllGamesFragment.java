package com.example.playerlink.all_games;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerlink.MainActivity;
import com.example.playerlink.R;
import com.example.playerlink.RAWG_api.DataService;
import com.example.playerlink.Result;
import com.example.playerlink.models.Game;
import com.example.playerlink.repositories.ProfileRepository;
import com.example.playerlink.repositories.ProfileRepositoryFirebase;
import com.example.playerlink.repositories.RepositoryCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class AllGamesFragment extends Fragment {

    private RecyclerView recyclerView;
    private GameAdapter gameAdapter;

    private ProfileRepository profileRepository = new ProfileRepositoryFirebase();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) requireActivity()).setBottomNavigationVisibility(View.GONE);
        ArrayList<Game> allGames = DataService.getAllGames();
        gameAdapter = new GameAdapter(allGames);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_games, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(gameAdapter);

        Button saveGameBtn = view.findViewById(R.id.save_games_btn);
        saveGameBtn.setOnClickListener(v -> saveSelectedGames());

    }

    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            // Handle the case where the user is not authenticated
            return null;
        }
    }

    private void saveSelectedGames() {
        String userId = getCurrentUserId();

        List<Game> selectedGames = new ArrayList<>();
        for (Game game : gameAdapter.getGames()) {
            if (game.isSelected()) {
                selectedGames.add(game);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Save Games");
        builder.setMessage("Are you sure you want to save the selected games?\n you can change it later in your profile");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profileRepository.updateGamesList(userId, selectedGames, new RepositoryCallback<Void>() {
                    @Override
                    public void onComplete(Result<Void> result) {
                        if (result instanceof Result.Success) {
                            // Handle success
                            Toast.makeText(requireContext(), "Games saved", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(requireView()).navigate(R.id.action_allGamesFragment_to_allUsersFragment2);
                        } else if (result instanceof Result.Error) {
                            // Handle error
                            Toast.makeText(requireContext(), "Error saving games", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
