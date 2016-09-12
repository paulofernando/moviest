package br.net.paulofernando.moviest.communication.services;

import br.net.paulofernando.moviest.communication.entities.Configuration;
import br.net.paulofernando.moviest.communication.entities.Genres;
import br.net.paulofernando.moviest.communication.entities.Images;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.communication.entities.Page;
import br.net.paulofernando.moviest.communication.entities.Videos;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesService {

    /**
     * Get the movie information for a specific movie.
     */
    @GET("movie/{id}")
    Call<Movie> summary(
            @Path("id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{id}")
    Call<Movie> summary2(
            @Path("id") int movieId,
            @Query("api_key") String apiKey
    );

    /**
     * Get the now playing for a specific page number.
     */
    @GET("movie/now_playing")
    Call<Page> nowPlaying(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    /**
     * Get the popular for a specific page number.
     */
    @GET("movie/popular")
    Call<Page> popular(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    /**
     * Get the top rated for a specific page number.
     */
    @GET("movie/top_rated")
    Call<Page> topRated(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    /**
     * Get the images for a specific movie.
     */
    @GET("movie/{id}/images")
    Call<Images> images(
            @Path("id") int movieId,
            @Query("api_key") String apiKey
    );

    /**
     * Get the videos for a specific movie.
     */
    @GET("movie/{id}/videos")
    Call<Videos> videos(
            @Path("id") int movieId,
            @Query("api_key") String apiKey
    );

    //--------------- search -----------------------

    /**
     * Searches by a movie based on a query
     */
    @GET("search/movie")
    Call<Page> search(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page);

    //--------------- genres -----------------------

    /**
     * Get the genres.
     */
    @GET("genre/movie/list")
    Call<Genres> genres(@Query("api_key") String apiKey);

    //--------------- configuration ----------------

    /**
     * Get the configuration of themoviedb API.
     */
    @GET("configuration")
    Call<Configuration> configuration(@Query("api_key") String apiKey);


}
