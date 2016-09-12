package br.net.paulofernando.moviest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.storage.CacheManager;

public class SplashScreenActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CacheManager.startCacheService(getApplicationContext());
        MovieDB.configureTMDB();
        MovieDB.configureGenres();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
