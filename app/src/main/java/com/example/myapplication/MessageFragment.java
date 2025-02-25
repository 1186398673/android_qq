package com.example.myapplication;

// MessageFragment.java
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.CardAdapter;
import com.example.myapplication.CardItem;
import com.example.myapplication.NewCardActivity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment {

    private static final int REQUEST_CODE_NEW_CARD = 1;
    private CardAdapter adapter;
    private CardViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // 初始化 RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CardAdapter(getContext(),new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 将 ViewModel 的作用域设置为 Activity，这样在同一个 Activity 内的所有 Fragment 都可以共享同一个 ViewModel，从而保持数据的持久性。
        viewModel = new ViewModelProvider(requireActivity()).get(CardViewModel.class);
        //viewModel = new ViewModelProvider(this).get(CardViewModel.class);
        CardDatabaseHelper dbHelper = new CardDatabaseHelper(getContext());
        List<CardItem> cardList = dbHelper.getCardList();
        viewModel.getCards(cardList).observe(getViewLifecycleOwner(), new Observer<List<CardItem>>() {
            @Override
            public void onChanged(List<CardItem> cards) {
                adapter.setCards(cards);
            }
        });

        // 新建卡片按钮点击事件
        Button buttonAddCard = view.findViewById(R.id.buttonAddCard);
        buttonAddCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NewCardActivity.class);
            startActivityForResult(intent, REQUEST_CODE_NEW_CARD);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_NEW_CARD && resultCode == getActivity().RESULT_OK && data != null) {
            CardItem newCard = (CardItem) data.getSerializableExtra("newCard");
            if (newCard != null) {
                viewModel.addCard(newCard);
                // 保存到数据库
                CardDatabaseHelper dbHelper = new CardDatabaseHelper(getContext());
                dbHelper.insertCard(newCard);


            }
        }
    }


}