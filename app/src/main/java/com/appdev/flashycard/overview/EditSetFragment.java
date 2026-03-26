package com.appdev.flashycard.overview;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.appdev.flashycard.R;
import com.appdev.flashycard.database.DatabaseHelper;
import com.appdev.flashycard.database.SessionManager;
import com.appdev.flashycard.overview.card.EditFlashcardAdapter;
import com.appdev.flashycard.overview.card.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class EditSetFragment extends Fragment {

    private long setId;
    private long userId;
    private RecyclerView rvEditCards;
    private EditFlashcardAdapter adapter;
    private List<Flashcard> flashcards;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    public static EditSetFragment newInstance(long setId) {
        EditSetFragment fragment = new EditSetFragment();
        Bundle args = new Bundle();
        args.putLong("SET_ID", setId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setId = getArguments().getLong("SET_ID");
        }
        dbHelper = new DatabaseHelper(getContext());
        sessionManager = new SessionManager(getContext());
        userId = getUserId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_set, container, false);
        rvEditCards = view.findViewById(R.id.rv_edit_flashcards);
        
        loadFlashcards();
        return view;
    }

    private void loadFlashcards() {
        flashcards = new ArrayList<>();
        Cursor cursor = dbHelper.getCardsBySet(setId);
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARD_ID));
                String term = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARD_TERM));
                String definition = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARD_DEFINITION));
                flashcards.add(new Flashcard(id, setId, term, definition));
            } while (cursor.moveToNext());
            cursor.close();
        }

        adapter = new EditFlashcardAdapter(flashcards, new EditFlashcardAdapter.OnCardActionListener() {
            @Override
            public void onSave(Flashcard card, int position) {
                if (card.getTerm().trim().isEmpty() || card.getDefinition().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Term and Definition cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (card.getId() == -1) {
                    // New card
                    long newId = dbHelper.addFlashcard(setId, userId, card.getTerm(), card.getDefinition());
                    if (newId != -1) {
                        Toast.makeText(getContext(), "Card added!", Toast.LENGTH_SHORT).show();
                        loadFlashcards();
                    }
                } else {
                    // Update existing card
                    int rows = dbHelper.updateFlashcard(card.getId(), card.getTerm(), card.getDefinition());
                    if (rows > 0) {
                        Toast.makeText(getContext(), "Card updated!", Toast.LENGTH_SHORT).show();
                        loadFlashcards();
                    }
                }
            }

            @Override
            public void onDelete(Flashcard card, int position) {
                if (card.getId() != -1) {
                    dbHelper.deleteFlashcard(card.getId());
                    Toast.makeText(getContext(), "Card deleted", Toast.LENGTH_SHORT).show();
                }
                flashcards.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, flashcards.size());
            }
        });
        rvEditCards.setAdapter(adapter);
    }

    public void addNewBlankCard() {
        flashcards.add(0, new Flashcard(-1, setId, "", ""));
        adapter.notifyItemInserted(0);
        rvEditCards.scrollToPosition(0);
    }

    private long getUserId() {
        String email = sessionManager.getUserEmail();
        if (email != null) {
            Cursor cursor = dbHelper.getUserDetailsByEmail(email);
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(0);
                cursor.close();
                return id;
            }
        }
        return -1;
    }
}
