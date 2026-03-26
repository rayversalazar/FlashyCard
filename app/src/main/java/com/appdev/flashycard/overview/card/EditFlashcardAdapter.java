package com.appdev.flashycard.overview.card;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appdev.flashycard.R;

import java.util.List;

public class EditFlashcardAdapter extends RecyclerView.Adapter<EditFlashcardAdapter.ViewHolder> {

    private List<Flashcard> flashcards;
    private OnCardActionListener actionListener;

    public interface OnCardActionListener {
        void onSave(Flashcard card, int position);
        void onDelete(Flashcard card, int position);
    }

    public EditFlashcardAdapter(List<Flashcard> flashcards, OnCardActionListener actionListener) {
        this.flashcards = flashcards;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flashcard_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flashcard card = flashcards.get(position);
        holder.etTerm.setText(card.getTerm());
        holder.etDefinition.setText(card.getDefinition());

        holder.btnSave.setOnClickListener(v -> {
            String newTerm = holder.etTerm.getText().toString();
            String newDef = holder.etDefinition.getText().toString();
            Flashcard updatedCard = new Flashcard(card.getId(), -1, newTerm, newDef);
            actionListener.onSave(updatedCard, position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            actionListener.onDelete(card, position);
        });
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etTerm, etDefinition;
        ImageButton btnSave, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etTerm = itemView.findViewById(R.id.et_term);
            etDefinition = itemView.findViewById(R.id.et_definition);
            btnSave = itemView.findViewById(R.id.btn_save_card);
            btnDelete = itemView.findViewById(R.id.btn_delete_card);
        }
    }
}
