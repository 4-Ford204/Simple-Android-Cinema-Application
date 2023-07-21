package com.example.testassignment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MovieActivity extends AppCompatActivity implements MovieAdapter.OnMovieListener {

    private DatabaseHandler database;
    private MovieAdapter movieAdapter;
    private ArrayList<Movie> movieArrayList;
    private TextView selectedMovieID;
    private EditText selectedTitle;
    private EditText selectedDirector;
    private EditText selectedYearRelease;
    private EditText selectedRating;
    private RecyclerView movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies);

        selectedMovieID = (TextView) findViewById(R.id.selectedMovieID);
        selectedTitle = (EditText) findViewById(R.id.selectedTitle);
        selectedDirector = (EditText) findViewById(R.id.selectedDirector);
        selectedYearRelease = (EditText) findViewById(R.id.selectedYearRelease);
        selectedRating = (EditText) findViewById(R.id.selectedRating);
        movies = (RecyclerView) findViewById(R.id.movies);
        Button addButton = (Button) findViewById(R.id.addButton);
        Button updateButton = (Button) findViewById(R.id.updateButton);
        Button removeButton = (Button) findViewById(R.id.removeButton);
        Button clearButton = (Button) findViewById(R.id.clearButton);
        EditText searchTitle = (EditText) findViewById(R.id.searchTitle);
        Button searchButton = (Button) findViewById(R.id.searchButton);

        database = new DatabaseHandler(this, "Movies.sqlite", null, 1);

        movieArrayList = GetMovies();

        movieAdapter = new MovieAdapter(movieArrayList, this);
        movies.setAdapter(movieAdapter);
        movies.setLayoutManager(new LinearLayoutManager(this));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MovieDataValidation()) return;

                String title = selectedTitle.getText().toString();
                String director = selectedDirector.getText().toString();
                int yearRelease = Integer.parseInt(selectedYearRelease.getText().toString());
                float rating = Float.parseFloat(selectedRating.getText().toString());

                AlertDialog.Builder dialog = new AlertDialog.Builder(MovieActivity.this);
                dialog.setTitle("Confirm Create");
                dialog.setMessage("Do you want to create this movie: \n" +
                        "\nTitle: " + title +
                        "\nDirector: " + director +
                        "\nYear Release: " + yearRelease +
                        "\nRating: " + rating);

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
                        database.QueryData("INSERT INTO Movies VALUES (null, '" + title + "', '" + director + "', " + yearRelease + ", " + rating + ")");
                        ResetAdapter();
                        Toast.makeText(getApplicationContext(), "Insert Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMovieID.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Choose Movie To Update", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!MovieDataValidation()) return;

                int movieID = Integer.parseInt(selectedMovieID.getText().toString());
                Movie movie = GetMovieByID(movieID);
                String title = selectedTitle.getText().toString();
                String director = selectedDirector.getText().toString();
                int yearRelease = Integer.parseInt(selectedYearRelease.getText().toString());
                float rating = Float.parseFloat(selectedRating.getText().toString());

                AlertDialog.Builder dialog = new AlertDialog.Builder(MovieActivity.this);
                dialog.setTitle("Confirm Update");
                dialog.setMessage("Do you want to update from this movie: \n" +
                        "\nTitle: " + movie.getTitle() +
                        "\nDirector: " + movie.getDirector() +
                        "\nYear Release: " + movie.getYearRelease() +
                        "\nRating: " + movie.getRating() +
                        "\n\nTo this movie: \n" +
                        "\nTitle: " + title +
                        "\nDirector: " + director +
                        "\nYear Release: " + yearRelease +
                        "\nRating: " + rating);

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
                        database.QueryData("UPDATE Movies SET Title = '" + title + "', Director = '" + director + "', YearRelease = " + yearRelease + ", Rating = " + rating + " WHERE MovieID = " + movieID);
                        ResetAdapter();
                        Toast.makeText(getApplicationContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMovieID.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Choose Movie To Delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                int movieID = Integer.parseInt(selectedMovieID.getText().toString());
                String title = selectedTitle.getText().toString();
                String director = selectedDirector.getText().toString();
                int yearRelease = Integer.parseInt(selectedYearRelease.getText().toString());
                float rating = Float.parseFloat(selectedRating.getText().toString());

                AlertDialog.Builder dialog = new AlertDialog.Builder(MovieActivity.this);
                dialog.setTitle("Confirm Delete");
                dialog.setMessage("Do you want to delete this movie: \n" +
                        "\nTitle: " + title +
                        "\nDirector: " + director +
                        "\nYear Release: " + yearRelease +
                        "\nRating: " + rating);

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
                        database.QueryData("DELETE FROM Movies WHERE MovieID = " + movieID);
                        database.QueryData("DELETE FROM Favorite WHERE MovieID = " + movieID);
                        ResetAdapter();
                        Toast.makeText(getApplicationContext(), "Delete Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearInformation();
                HideKeyboard();
            }
        });

        clearButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MovieActivity.this);
                dialog.setTitle("Confirm Exit");
                dialog.setMessage("Do you want to exit?");

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
                        startActivity(new Intent(MovieActivity.this, MainActivity.class));
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchTitle.getText().toString();
                if (keyword.isEmpty()) {
                    ResetAdapter();
                } else {
                    movieArrayList = GetMoviesByKeyword(keyword);
                    movieAdapter.setMovieAdapterData(movieArrayList, MovieActivity.this);
                    ClearInformation();
                    HideKeyboard();
                }
            }
        });
    }

    private void ResetAdapter() {
        movieArrayList = GetMovies(); // Reset Movie Array To Bind In mOnMovieListener
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

    private Movie GetMovieByID(int id) {
        Cursor cursor = database.GetData("SELECT * FROM Movies WHERE MovieID = " + id);

        while (cursor.moveToNext()) {
            int movieID = cursor.getInt(cursor.getColumnIndex("MovieID"));
            String title = cursor.getString(cursor.getColumnIndex("Title"));
            String director = cursor.getString(cursor.getColumnIndex("Director"));
            int yearRelease = cursor.getInt(cursor.getColumnIndex("YearRelease"));
            float rating = cursor.getFloat(cursor.getColumnIndex("Rating"));
            return new Movie(movieID, title, director, yearRelease, rating);
        }

        return null;
    }

    public ArrayList<Movie> GetMovies() {
        Cursor cursor = database.GetData("SELECT * FROM Movies ORDER BY YearRelease DESC, Rating DESC");

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

    public ArrayList<Movie> GetMoviesByKeyword(String keyword) {
        Cursor cursor = database.GetData("SELECT * FROM Movies WHERE Title LIKE '%" + keyword + "%' ORDER BY YearRelease DESC, Rating DESC");

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

    private boolean MovieDataValidation() {
        try {
            Integer.parseInt(selectedYearRelease.getText().toString());
            Float.parseFloat(selectedRating.getText().toString());
            if (selectedTitle.getText().toString().isEmpty() || selectedDirector.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all blank", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    public void OnClickMovieView(View view) {
        HideKeyboard();
    }

    private void HideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

}
