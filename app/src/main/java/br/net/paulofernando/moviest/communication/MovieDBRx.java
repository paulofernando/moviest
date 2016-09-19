package br.net.paulofernando.moviest.communication;

import br.net.paulofernando.moviest.communication.services.MoviesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDBRx {

    public static final String API_KEY = "e5cbc2790e7c9f44215b11a311216ed2";
    public static final String YOUTUBE_KEY = "AIzaSyAvRH6dhpunY1wqTCCsSQriN_nWD-YUXOI";
    public static final String API_HOST = "api.themoviedb.org";
    public static final String API_VERSION = "3";
    public static final String API_URL = "https://" + API_HOST + "/" + API_VERSION + "/";

    private static MovieDBRx instance;

    private MovieDBRx() {}

    public static MovieDBRx getInstance() {
        if(instance == null) {
            instance = new MovieDBRx();
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

    public MoviesService moviesService() {
        return getRetrofit().create(MoviesService.class);
    }

}
