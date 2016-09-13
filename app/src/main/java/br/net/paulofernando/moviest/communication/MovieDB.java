package br.net.paulofernando.moviest.communication;

import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import java.io.IOException;

import br.net.paulofernando.moviest.communication.entities.Configuration;
import br.net.paulofernando.moviest.communication.entities.Genre;
import br.net.paulofernando.moviest.communication.entities.Genres;
import br.net.paulofernando.moviest.communication.services.MoviesService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDB {

    public static final String API_KEY = "e5cbc2790e7c9f44215b11a311216ed2";
    public static final String YOUTUBE_KEY = "AIzaSyAvRH6dhpunY1wqTCCsSQriN_nWD-YUXOI";
    public static final String API_HOST = "api.themoviedb.org";
    public static final String API_VERSION = "3";
    public static final String API_URL = "https://" + API_HOST + "/" + API_VERSION + "/";

    private static final String TAG = "MovieDB";
    private static final String CACHE_CONFIG = "config";
    private static final String CACHE_GENRES = "genres";

    private static Configuration configuration;
    private static Genres genres;

    public enum Services {
        NowPlaying, Popular, TopRated, Configuration, Images, Videos, Genres, SearchMovie, Summary, SummaryWithCredits
    }

    public static final String MOVIE_DETAILS = "movie_details";
    public static final String COLLECTION_DETAILS = "collection_details";

    private static MovieDB instance;

    private MovieDB() {}

    public static MovieDB getInstance() {
        if(instance == null) {
            instance = new MovieDB();
        }
        return instance;
    }

    private Retrofit retrofit;

    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = retrofitBuilder().build();
        }
        return retrofit;
    }

    protected Retrofit.Builder retrofitBuilder() {
        return new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create());
    }

    // --------------- services --------------------

    public MoviesService moviesService() {
        return getRetrofit().create(MoviesService.class);
    }

    //-----------------------------------------------

    public static void setConfiguration(Configuration configuration) {
        MovieDB.configuration = configuration;
    }

    public static void setGenres(Genres genres) {
        MovieDB.genres = genres;
    }

    public static Configuration getConfiguration() {
        return MovieDB.configuration;
    }

    public static String getGenreNameByID(int genreID) {
        if(genres != null) {
            for (Genre g : genres.genres) {
                if (g.id == genreID) {
                    return g.name;
                }
            }
        }
        return "";
    }

    public static void configureTMDB() {
        try {
            if (Reservoir.contains(CACHE_CONFIG)) {
                Reservoir.getAsync(CACHE_CONFIG, Configuration.class, new ReservoirGetCallback<Configuration>() {
                    @Override
                    public void onSuccess(Configuration configuration) {
                        MovieDB.getInstance().setConfiguration(configuration);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        configureTMDBFromAPI();
                    }
                });
            } else {
                configureTMDBFromAPI();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void configureTMDBFromAPI() {
        Call<Configuration> callConfig = MovieDB.getInstance().moviesService().configuration(MovieDB.API_KEY);
        callConfig.enqueue(new Callback<Configuration>() {
            @Override
            public void onResponse(Call<Configuration> call, Response<Configuration> response) {
                if (response.isSuccessful()) {
                    try {
                        Reservoir.put(CACHE_CONFIG, response.body());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MovieDB.getInstance().setConfiguration(response.body());
                }
            }

            @Override
            public void onFailure(Call<Configuration> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    public static void configureGenres() {
        try {
            if (Reservoir.contains(CACHE_GENRES)) {
                Reservoir.getAsync(CACHE_GENRES, Genres.class, new ReservoirGetCallback<Genres>() {
                    @Override
                    public void onSuccess(Genres genres) {
                        MovieDB.getInstance().setGenres(genres);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        configureGenresFromAPI();
                    }
                });
            } else {
                configureGenresFromAPI();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void configureGenresFromAPI() {
        Call<Genres> callGenres = MovieDB.getInstance().moviesService().genres(MovieDB.API_KEY);
        callGenres.enqueue(new Callback<Genres>() {
            @Override
            public void onResponse(Call<Genres> call, Response<Genres> response) {
                if (response.isSuccessful()) {
                    try {
                        Reservoir.put(CACHE_GENRES, response.body());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MovieDB.getInstance().setGenres(response.body());
                }
            }
            @Override
            public void onFailure(Call<Genres> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
}
