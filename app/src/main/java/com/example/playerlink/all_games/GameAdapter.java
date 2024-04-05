package com.example.playerlink.all_games;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.playerlink.R;
import com.example.playerlink.models.Game;
import java.util.ArrayList;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private List<Game> games;

    public GameAdapter(List<Game> games) {
        this.games = games;
    }
    public void setGames(List<Game> games) {
        this.games = games;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_card, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = games.get(position);
        holder.bind(game);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {
        private ImageView gameImage;
        private TextView gameTitle;
        private TextView rating;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.game_image);
            gameTitle = itemView.findViewById(R.id.game_title);
            rating = itemView.findViewById(R.id.rating);
        }

        public void bind(Game game) {
            gameTitle.setText(game.getName());
            rating.setText(String.valueOf(game.getRating()));
            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(game.getImage())
                    .into(gameImage);
        }
    }
}
