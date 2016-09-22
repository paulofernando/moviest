package br.net.paulofernando.moviest.communication.services;

import br.net.paulofernando.moviest.communication.entities.Collection;
import br.net.paulofernando.moviest.communication.entities.Configuration;
import br.net.paulofernando.moviest.communication.entities.Genres;
import br.net.paulofernando.moviest.communication.entities.Images;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.communication.entities.MovieWithCredits;
import br.net.paulofernando.moviest.communication.entities.Page;
import br.net.paulofernando.moviest.communication.entities.Videos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import rx.Observable;

public interface TMDBService {

    @GET("movie/{id}")
    Observable<Movie> summaryRx(
            @Path("id") int movieId,
            @Query("api_key") String apiKey
    );

    /**
     * Get the movie information for a specific movie.
     */
    @GET("movie/{id}")
    Observable<MovieWithCredits> summaryWithAppendRx(
            @Path("id") int movieId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String appendToResponse
    );


    /**
     * Get the now playing for a specific pageNumber number.
     */
    @GET("movie/now_playing")
    Observable<Page> nowPlayingRx(
            @Query("api_key") String apiKey,
            @Query("pageNumber") int page
    );

    /**
     * Get the popular for a specific pageNumber number.
     */
    @GET("movie/popular")
    Observable<Page> popularRx(
            @Query("api_key") String apiKey,
            @Query("pageNumber") int page
    );

    /**
     * Get the top rated for a specific pageNumber number.
     */
    @GET("movie/top_rated")
    Observable<Page> topRatedRx(
            @Query("api_key") String apiKey,
            @Query("pageNumber") int page
    );

    /**
     * Get the images for a specific movie.
     */
    @GET("movie/{id}/images")
    Observable<Images> imagesRx(
            @Path("id") int movieId,
            @Query("api_key") String apiKey
    );

    /**
     * Get the videos for a specific movie.
     */
    @GET("movie/{id}/videos")
    Observable<Videos> videosRx(
            @Path("id") int movieId,
            @Query("api_key") String apiKey
    );

    //--------------- search -----------------------

    /**
     * Searches by a movie based on a query
     */
    @GET("search/movie")
    Observable<Page> searchRx(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("pageNumber") int page);

    //--------------- genres -----------------------

    /**
     * Get the genres.
     */
    @GET("genre/movie/list")
    Observable<Genres> genresRx(@Query("api_key") String apiKey);

    //--------------- configuration ----------------

    /**
     * Get the configuration of themoviedb API.
     */
    @GET("configuration")
    Observable<Configuration> configurationRx(@Query("api_key") String apiKey);

}
