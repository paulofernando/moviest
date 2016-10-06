package br.net.paulofernando.moviest.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.net.paulofernando.moviest.data.remote.TMDB;
import br.net.paulofernando.moviest.data.CacheManager;

public class SplashScreenActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CacheManager.startCacheService(getApplicationContext());
        TMDB.configureTMDB();
        TMDB.configureGenres();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
