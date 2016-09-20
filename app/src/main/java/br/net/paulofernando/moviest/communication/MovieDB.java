package br.net.paulofernando.moviest.communication;

import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import java.io.IOException;

import br.net.paulofernando.moviest.communication.entities.Configuration;
import br.net.paulofernando.moviest.communication.entities.Genre;
import br.net.paulofernando.moviest.communication.entities.Genres;
import br.net.paulofernando.moviest.communication.services.MoviesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
            retrofit = new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(API_URL)
                    .build();
        }
        return retrofit;
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
        MovieDB.getInstance().moviesService().configurationRx(MovieDB.API_KEY)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Configuration>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage());
                }

                @Override
                public void onNext(Configuration configuration) {
                    try {
                        Reservoir.put(CACHE_CONFIG, configuration);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MovieDB.getInstance().setConfiguration(configuration);
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
        MovieDB.getInstance().moviesService().genresRx(MovieDB.API_KEY)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<Genres>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage());
                }

                @Override
                public void onNext(Genres genres) {
                    try {
                        Reservoir.put(CACHE_GENRES, genres);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    MovieDB.getInstance().setGenres(genres);
                }
            });
    }

}
