package com.example.testassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private ArrayList<Movie> movies;
    private OnMovieListener mOnMovieListener;

    public MovieAdapter(ArrayList<Movie> movies, OnMovieListener mOnMovieListener) {
        this.movies = movies;
        this.mOnMovieListener = mOnMovieListener;
    }

    public void setMovieAdapterData(ArrayList<Movie> movies, OnMovieListener mOnMovieListener) {
        this.movies = movies;
        this.mOnMovieListener = mOnMovieListener;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView director;
        private TextView yearRelease;
        private TextView rating;
        private OnMovieListener onMovieListener;

        public ViewHolder(@NonNull View itemView, OnMovieListener onMovieListener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            director = (TextView) itemView.findViewById(R.id.director);
            yearRelease = (TextView) itemView.findViewById(R.id.yearRelease);
            rating = (TextView) itemView.findViewById(R.id.rating);
            this.onMovieListener = onMovieListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onMovieListener.onMovieClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View movieView = inflater.inflate(R.layout.movie_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(movieView, mOnMovieListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        TextView title = holder.title;
        title.setText(movie.getTitle());
        TextView director = holder.director;
        director.setText(movie.getDirector());
        TextView yearRelease = holder.yearRelease;
        yearRelease.setText(movie.getYearRelease() + "");
        TextView rating = holder.rating;
        rating.setText(movie.getRating() + "");
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public interface OnMovieListener {
        void onMovieClick(int position);
    }

}
