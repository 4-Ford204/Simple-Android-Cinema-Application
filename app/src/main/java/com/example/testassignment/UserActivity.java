package com.example.testassignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity implements MovieAdapter.OnMovieListener {

    private SharedPreferences prf;
    private Intent intent;
    private DatabaseHandler database;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieArrayList;
    private BottomNavigationView navigationView;
    private TextView selectedMovieID;
    private TextView selectedTitle;
    private TextView selectedDirector;
    private TextView selectedYearRelease;
    private TextView selectedRating;
    private RecyclerView movies;
    private EditText searchTitle;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        prf = getSharedPreferences("user_details", MODE_PRIVATE);
        navigationView = findViewById(R.id.navigation);
        selectedMovieID = (TextView) findViewById(R.id.selectedMovieID);
        selectedTitle = (TextView) findViewById(R.id.selectedTitle);
        selectedDirector = (TextView) findViewById(R.id.selectedDirector);
        selectedYearRelease = (TextView) findViewById(R.id.selectedYearRelease);
        selectedRating = (TextView) findViewById(R.id.selectedRating);
        movies = (RecyclerView) findViewById(R.id.movies);
        Button addButton = (Button) findViewById(R.id.addButton);
        Button removeButton = (Button) findViewById(R.id.removeButton);
        Button clearButton = (Button) findViewById(R.id.clearButton);
        searchTitle = (EditText) findViewById(R.id.searchTitle);
        searchButton = (Button) findViewById(R.id.searchButton);

        database = new DatabaseHandler(this, "Movies.sqlite", null, 1);

        movieArrayList = GetAllMovies();

        movieAdapter = new MovieAdapter(movieArrayList, this);
        movies.setAdapter(movieAdapter);
        movies.setLayoutManager(new LinearLayoutManager(this));

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                ClearInformation();
                if (item.getItemId() == R.id.action_all_movies) {
                    addButton.setVisibility(View.VISIBLE);
                    removeButton.setVisibility(View.GONE);
                    ResetAdapter(GetAllMovies());
                    SetSearchButtonOnClick(0);
                } else if (item.getItemId() == R.id.action_favorite_movies) {
                    addButton.setVisibility(View.GONE);
                    removeButton.setVisibility(View.VISIBLE);
                    ResetAdapter(GetFavoriteMovies());
                    SetSearchButtonOnClick(1);
                } else if (item.getItemId() == R.id.action_logout) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
                    dialog.setTitle("Confirm Logout");
                    dialog.setMessage("Do you want to logout?");

                    dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HideKeyboard();
                            dialog.cancel();
                        }
                    });

                    dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prf = getSharedPreferences("user_details", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prf.edit();
                            editor.clear();
                            editor.commit();
                            intent = new Intent(UserActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }

                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMovieID.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Choose A Movie", Toast.LENGTH_SHORT).show();
                } else {
                    InsertFavorite();
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMovieID.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Choose A Movie", Toast.LENGTH_SHORT).show();
                } else {
                    RemoveFavorite();
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearInformation();
                HideKeyboard();
            }
        });
    }

    private void SetSearchButtonOnClick(int status) {
        if (status == 0) {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String keyword = searchTitle.getText().toString();
                    if (keyword.isEmpty()) {
                        ResetAdapter(GetAllMovies());
                    } else {
                        movieArrayList = GetAllMoviesByKeyword(keyword);
                        movieAdapter.setMovieAdapterData(movieArrayList, UserActivity.this);
                        ClearInformation();
                        HideKeyboard();
                    }
                }
            });
        } else {
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String keyword = searchTitle.getText().toString();
                    if (keyword.isEmpty()) {
                        ResetAdapter(GetFavoriteMovies());
                    } else {
                        movieArrayList = GetFavoriteMoviesByKeyword(keyword);
                        movieAdapter.setMovieAdapterData(movieArrayList, UserActivity.this);
                        ClearInformation();
                        HideKeyboard();
                    }
                }
            });
        }
    }

    private void ResetAdapter(ArrayList<Movie> newMovieArrayList) {
        movieArrayList = newMovieArrayList;
        movieAdapter.setMovieAdapterData(movieArrayList, this);
        ClearInformation();
        HideKeyboard();
    }

    private void ClearInformation() {
        selectedMovieID.setText("");
        selectedTitle.setText("");
        selectedDirector.setText("");
        selectedYearRelease.setText("");
        selectedRating.setText("");
    }

    public ArrayList<Movie> GetAllMovies() {
        Cursor cursor = database.GetData("SELECT * FROM Movies WHERE MovieID NOT IN (SELECT MovieID FROM Favorite WHERE UserID = " +
                prf.getInt("UserID", 0) + ") ORDER BY YearRelease DESC, Rating DESC");

        ArrayList<Movie> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int movieID = cursor.getInt(cursor.getColumnIndex("MovieID"));
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            String director = cursor.getString(cursor.getColumnIndex("Director"));
            int yearRelease = cursor.getInt(cursor.getColumnIndex("YearRelease"));
            float rating = cursor.getFloat(cursor.getColumnIndex("Rating"));
            movieList.add(new Movie(movieID, title, director, yearRelease, rating));
        }

        return movieList;
    }

    public ArrayList<Movie> GetAllMoviesByKeyword(String keyword) {
        Cursor cursor = database.GetData("SELECT * FROM Movies WHERE Title LIKE '%" + keyword + "%' AND MovieID NOT IN (SELECT MovieID FROM Favorite WHERE UserID = " +
                prf.getInt("UserID", 0) + ") ORDER BY YearRelease DESC, Rating DESC");

        ArrayList<Movie> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int movieID = cursor.getInt(cursor.getColumnIndex("MovieID"));
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            String director = cursor.getString(cursor.getColumnIndex("Director"));
            int yearRelease = cursor.getInt(cursor.getColumnIndex("YearRelease"));
            float rating = cursor.getFloat(cursor.getColumnIndex("Rating"));
            movieList.add(new Movie(movieID, title, director, yearRelease, rating));
        }

        return movieList;
    }

    public ArrayList<Movie> GetFavoriteMovies() {
        Cursor cursor = database.GetData("SELECT * FROM Movies WHERE MovieID IN (SELECT MovieID FROM Favorite WHERE UserID = " +
                prf.getInt("UserID", 0) + ") ORDER BY YearRelease DESC, Rating DESC");

        ArrayList<Movie> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int movieID = cursor.getInt(cursor.getColumnIndex("MovieID"));
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            String director = cursor.getString(cursor.getColumnIndex("Director"));
            int yearRelease = cursor.getInt(cursor.getColumnIndex("YearRelease"));
            float rating = cursor.getFloat(cursor.getColumnIndex("Rating"));
            movieList.add(new Movie(movieID, title, director, yearRelease, rating));
        }

        return movieList;
    }

    public ArrayList<Movie> GetFavoriteMoviesByKeyword(String keyword) {
        Cursor cursor = database.GetData("SELECT * FROM Movies WHERE Title LIKE '%" + keyword + "%' AND MovieID IN (SELECT MovieID FROM Favorite WHERE UserID = " +
                prf.getInt("UserID", 0) + ") ORDER BY YearRelease DESC, Rating DESC");

        ArrayList<Movie> movieList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int movieID = cursor.getInt(cursor.getColumnIndex("MovieID"));
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            String director = cursor.getString(cursor.getColumnIndex("Director"));
            int yearRelease = cursor.getInt(cursor.getColumnIndex("YearRelease"));
            float rating = cursor.getFloat(cursor.getColumnIndex("Rating"));
            movieList.add(new Movie(movieID, title, director, yearRelease, rating));
        }

        return movieList;
    }

    public void InsertFavorite() {
        database.QueryData("INSERT INTO Favorite VALUES (null, " + prf.getInt("UserID", 0) + ", " + Integer.parseInt(selectedMovieID.getText().toString()) + ")");
        ResetAdapter(GetAllMovies());
        Toast.makeText(getApplicationContext(), "Insert To Favorite Movies", Toast.LENGTH_SHORT).show();
    }

    public void RemoveFavorite() {
        database.QueryData("DELETE FROM Favorite WHERE UserID = " + prf.getInt("UserID", 0) + " AND MovieID = " + Integer.parseInt(selectedMovieID.getText().toString()));
        ResetAdapter(GetFavoriteMovies());
        Toast.makeText(getApplicationContext(), "Remove From Favorite Movies", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMovieClick(int position) {
        Movie movie = movieArrayList.get(position);
        selectedMovieID.setText(movie.getMovieID() + "");
        selectedTitle.setText(movie.getTitle());
        selectedDirector.setText(movie.getDirector());
        selectedYearRelease.setText(movie.getYearRelease() + "");
        selectedRating.setText(movie.getRating() + "");
    }

    public void OnClickUserView(View view) {
        HideKeyboard();
    }

    private void HideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

}
