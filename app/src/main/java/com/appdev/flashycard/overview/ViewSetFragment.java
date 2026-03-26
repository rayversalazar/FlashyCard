package com.appdev.flashycard.overview;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.appdev.flashycard.R;
import com.appdev.flashycard.database.DatabaseHelper;
import com.appdev.flashycard.overview.card.Flashcard;
import com.appdev.flashycard.overview.card.FlashcardAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewSetFragment extends Fragment {

    private long setId;
    private RecyclerView rvCards;
    private FlashcardAdapter adapter;
    private List<Flashcard> flashcards;
    private DatabaseHelper dbHelper;
    private LinearLayout layoutEmpty;

    public static ViewSetFragment newInstance(long setId) {
        ViewSetFragment fragment = new ViewSetFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_set, container, false);
        rvCards = view.findViewById(R.id.rv_flashcards);
        layoutEmpty = view.findViewById(R.id.layout_empty_state);
        
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

        if (flashcards.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvCards.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvCards.setVisibility(View.VISIBLE);
        }

        adapter = new FlashcardAdapter(flashcards);
        rvCards.setAdapter(adapter);
    }
}
