package com.example.testassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private Intent intent;
    private DatabaseHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        database = new DatabaseHandler(this, "Movies.sqlite", null, 1);
        //useDatabase();

        EditText userName = (EditText) findViewById(R.id.userName);
        EditText password = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        TextView error = (TextView) findViewById(R.id.error);

        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        intent = new Intent(MainActivity.this, UserActivity.class);
        if (pref.contains("UserID")) {
            startActivity(intent);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().equals("admin") && password.getText().toString().equals("123")) {
                    error.setText("");
                    userName.setText("");
                    password.setText("");
                    intent = new Intent(MainActivity.this, MovieActivity.class);
                    startActivity(intent);
                } else {
                    ArrayList<User> userList = GetUsers();
                    User loginUser = null;

                    for (User user : userList) {
                        if (userName.getText().toString().equals(user.getUserName()) && password.getText().toString().equals(user.getPassword())) {
                            loginUser = user;
                            error.setText("");
                            userName.setText("");
                            password.setText("");
                            break;
                        }
                    }

                    if (loginUser == null) {
                        error.setText("Incorrect UserName or Password");
                        userName.setText("");
                        password.setText("");
                    } else {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("UserID", loginUser.getUserID());
                        editor.commit();
                        intent = new Intent(MainActivity.this, UserActivity.class);
                        startActivity(intent);
                    }
                }
                HideKeyboard();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Shut down to cancel application", Toast.LENGTH_SHORT).show();
                HideKeyboard();
            }
        });
    }

    private void useDatabase() {
        // Table User
        database.QueryData("CREATE TABLE IF NOT EXISTS Users(" +
                "UserID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserName VARCHAR(50), " +
                "Password VARCHAR(50))");
        database.QueryData("INSERT INTO Users VALUES (null, 'duy@gmail.com', '123')");
        database.QueryData("INSERT INTO Users VALUES (null, 'ford@gmail.com', '123')");

        // Table Movie
        database.QueryData("CREATE TABLE IF NOT EXISTS Movies(" +
                "MovieID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title VARCHAR(100), " +
                "Director VARCHAR(50), " +
                "YearRelease INTEGER, " +
                "Rating REAL)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Black Panther', 'Ryan Coogler', 2018, 9.6)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Avengers: Infinity War', 'Anthony Russo, Joe Russo', 2018, 8.5)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Avengers: Endgame', 'Anthony Russo, Joe Russo', 2019, 9.4)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Spider-Man: No Way Home', 'Jon Watts', 2021, 9.3)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Captain America: Civil War', 'Anthony Russo, Joe Russo', 2016, 9.0)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Doctor Strange', 'Scott Derrickson', 2016, 8.9)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Ant-Man And The Wasp', 'Peyton Reed', 2018, 8.7)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Black Panther: Wakanda Forever', 'Ryan Coogler', 2022, 8.4)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Iron Man', 'Jon Favreau', 2008, 9.4)");
        database.QueryData("INSERT INTO Movies VALUES (null, 'Thor: Ragnarok', 'Taika Waititi', 2017, 9.3)");

        //Table Favorite
        database.QueryData("CREATE TABLE IF NOT EXISTS Favorite(" +
                "Favorite INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "UserID INTEGER, " +
                "MovieID INTEGER)");
        database.QueryData("INSERT INTO Favorite VALUES (null, 1, 1)");
    }

    public ArrayList<User> GetUsers() {
        Cursor cursor = database.GetData("SELECT * FROM Users");

        ArrayList<User> userList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int userID = cursor.getInt(cursor.getColumnIndex("UserID"));
            String userName = cursor.getString(cursor.getColumnIndex("UserName"));
            String password = cursor.getString(cursor.getColumnIndex("Password"));
            userList.add(new User(userID, userName, password));
        }

        return userList;
    }

    public void OnClickLoginView(View view) {
        HideKeyboard();
    }

    private void HideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

}