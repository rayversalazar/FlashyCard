package com.appdev.flashycard.main.set;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appdev.flashycard.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class FlashcardSetAdapter extends RecyclerView.Adapter<FlashcardSetAdapter.ViewHolder> {

    private List<FlashcardSet> flashcardSets;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FlashcardSet set);
    }

    public FlashcardSetAdapter(List<FlashcardSet> flashcardSets, OnItemClickListener listener) {
        this.flashcardSets = flashcardSets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard_set, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlashcardSet set = flashcardSets.get(position);
        holder.textTitle.setText(set.getTitle());
        holder.cardView.setCardBackgroundColor(Color.parseColor(set.getColorHex()));
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(set);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flashcardSets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_set_title);
            cardView = (MaterialCardView) itemView;
        }
    }
}
