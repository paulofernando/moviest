package br.net.paulofernando.moviest.data.remote;

import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.anupcowkur.reservoir.ReservoirGetCallback;

import java.io.IOException;

import br.net.paulofernando.moviest.BuildConfig;
import br.net.paulofernando.moviest.data.entities.Configuration;
import br.net.paulofernando.moviest.data.entities.Genre;
import br.net.paulofernando.moviest.data.entities.Genres;
import br.net.paulofernando.moviest.data.remote.services.TMDBService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TMDB {

    public static final String API_KEY = BuildConfig.TMDB_KEY;
    public static final String YOUTUBE_KEY = BuildConfig.YOUTUBE_KEY;
    public static final String API_HOST = "api.themoviedb.org";
    public static final String API_VERSION = "3";
    public static final String API_URL = "https://" + API_HOST + "/" + API_VERSION + "/";

    private static final String TAG = "TMDB";
    private static final String CACHE_CONFIG = "config";
    private static final String CACHE_GENRES = "genres";

    // Poster image sizes
    public static final String POSTER_SIZE_W92 = "w92";
    public static final String POSTER_SIZE_W154 = "w154";
    public static final String POSTER_SIZE_W185 = "w185";
    public static final String POSTER_SIZE_W342 = "w342";
    public static final String POSTER_SIZE_W500 = "w500";
    public static final String POSTER_W780 = "w780";
    public static final String POSTER_SIZE_ORIGINAL = "original";

    // Backdrop image sizes
    public static final String BACKDROP_SIZE_W300 = "w300";
    public static final String BACKDROP_SIZE_W780 = "w780";
    public static final String BACKDROP_SIZE_W1280 = "w1280";
    public static final String BACKDROP_SIZE_ORIGINAL = "original";

    // recommended for most phones:
    public static final String SIZE_DEFAULT = POSTER_SIZE_W342;

    private static Configuration configuration;
    private static Genres genres;

    public enum Services {
        NowPlayingService, PopularService, TopRatedService, ConfigurationService, ImagesService,
        VideosService, GenresService, SearchMovieService, SummaryService,
        SummaryWithCreditsService, CollectionsService
    }

    public static final String MOVIE_DETAILS = "movie_details";
    public static final String COLLECTION_DETAILS = "collection_details";

    private static TMDB instance;

    private TMDB() {}

    public static TMDB getInstance() {
        if(instance == null) {
            instance = new TMDB();
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

    public TMDBService moviesService() {
        return getRetrofit().create(TMDBService.class);
    }

    //-----------------------------------------------

    public static void setConfiguration(Configuration configuration) {
        TMDB.configuration = configuration;
    }

    public static void setGenres(Genres genres) {
        TMDB.genres = genres;
    }

    public static Configuration getConfiguration() {
        return TMDB.configuration;
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
                        TMDB.getInstance().setConfiguration(configuration);
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
        TMDB.getInstance().moviesService().configurationRx(TMDB.API_KEY)
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
                    TMDB.getInstance().setConfiguration(configuration);
                }
            });
    }

    public static void configureGenres() {
        try {
            if (Reservoir.contains(CACHE_GENRES)) {
                Reservoir.getAsync(CACHE_GENRES, Genres.class, new ReservoirGetCallback<Genres>() {
                    @Override
                    public void onSuccess(Genres genres) {
                        TMDB.getInstance().setGenres(genres);
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
        TMDB.getInstance().moviesService().genresRx(TMDB.API_KEY)
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
                    TMDB.getInstance().setGenres(genres);
                }
            });
    }

}
