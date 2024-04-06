package com.example.playerlink.all_games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.playerlink.R;
import com.example.playerlink.RAWG_api.DataService;
import com.example.playerlink.models.Game;

import java.util.ArrayList;
import java.util.List;

public class AllGamesFragment extends Fragment {

    private RecyclerView recyclerView;
    private GameAdapter gameAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void saveSelectedGames() {
        List<Game> selectedGames = new ArrayList<>();
        for (Game game : gameAdapter.getGames()) {
            if (game.isSelected()) {
                selectedGames.add(game);
            }
        }

        // Write selectedGames to Firebase
        // Example: DatabaseReference gamesRef = FirebaseDatabase.getInstance().getReference("games");
        //          gamesRef.setValue(selectedGames);
    }
}
