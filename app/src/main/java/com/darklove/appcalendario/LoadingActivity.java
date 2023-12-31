package com.darklove.appcalendario;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.darklove.appcalendario.requests.CourseRequest;
import com.darklove.appcalendario.requests.UnauthorizedException;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        SharedPreferences sharedPreferences = getSharedPreferences("userdata", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        int userId = sharedPreferences.getInt("user_id", 0);

        if (token == null || userId == 0) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                CourseRequest courseRequest = new CourseRequest(token, userId);
                String data = courseRequest.getData();
                HashMap<String, String> courses = courseRequest.getCourses(data);
                UserData.getInstance().setCourses(courses);
            } catch(UnauthorizedException e) {
                throw new CompletionException(e);
            }
        }).thenRunAsync(() -> {
            Intent intent = new Intent(this, CalendarActivity.class);
            startActivity(intent);
            finish();
        }).exceptionally((e) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return null;
        });

    }

}