package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    private List<Book> books;
    private OnItemClickListener listener;
    private Context context;

    public BookAdapter(Context context, List<Book> books, OnItemClickListener listener) {
        this.context = context;
        this.books = books;
        this.listener = listener;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        public ImageView coverImageView;
        public TextView titleTextView;

        public BookViewHolder(View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.bookCoverImageView);
            titleTextView = itemView.findViewById(R.id.bookTitleTextView);
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = books.get(position);
        holder.coverImageView.setImageResource(currentBook.getCoverResId());
        holder.titleTextView.setText(currentBook.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentBook));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}