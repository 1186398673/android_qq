package com.example.myapplication;

// CardViewModel.java
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.CardItem;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class CardViewModel extends ViewModel {
    private MutableLiveData<List<CardItem>> cards;

    public LiveData<List<CardItem>> getCards(List<CardItem> cardList) {
        if (cards == null) {
            cards = new MutableLiveData<>();
            loadCards(cardList);
        }
        return cards;
    }

    public void loadCards(List<CardItem> cardList) {
        // 如果需要，可以选择将预定义的卡片保存到数据库
        // saveCardsToDatabase(cardList);

        // 更新 LiveData
        cards.setValue(cardList);
    }

    public void addCard(CardItem card) {
        List<CardItem> updatedList = new ArrayList<>();
        if (cards.getValue() != null) {
            updatedList.addAll(cards.getValue());
        }
        updatedList.add(card);
        cards.setValue(updatedList);
    }
}