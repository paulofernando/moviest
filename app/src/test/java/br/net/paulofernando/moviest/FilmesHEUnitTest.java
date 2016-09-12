package br.net.paulofernando.moviest;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import br.net.paulofernando.moviest.communication.MovieDB;
import br.net.paulofernando.moviest.communication.entities.Configuration;
import br.net.paulofernando.moviest.communication.entities.Genres;
import br.net.paulofernando.moviest.communication.entities.Movie;
import br.net.paulofernando.moviest.communication.entities.Page;
import retrofit2.Call;

public class FilmesHEUnitTest {

    @Test
    public void test_summary() throws IOException {
        Call<Movie> call = MovieDB.getInstance().moviesService().summary(DataToTest.MOVIE_ID, MovieDB.API_KEY);
        Movie movie = call.execute().body();
        Assert.assertEquals(movie.title, DataToTest.MOVIE_TITLE);
    }

    @Test
    public void test_now_playing() throws IOException {
        Call<Page> call = MovieDB.getInstance().moviesService().nowPlaying(MovieDB.API_KEY, 1);
        Page nowPlayingPage = call.execute().body();
        Assert.assertNotNull(nowPlayingPage.movies.get(0));
    }

    @Test
    public void test_popular() throws IOException {
        Call<Page> call = MovieDB.getInstance().moviesService().popular(MovieDB.API_KEY, 1);
        Page nowPlayingPage = call.execute().body();
        Assert.assertNotNull(nowPlayingPage.movies.get(0));
    }

    @Test
    public void test_top_rated() throws IOException {
        Call<Page> call = MovieDB.getInstance().moviesService().topRated(MovieDB.API_KEY, 1);
        Page nowPlayingPage = call.execute().body();
        Assert.assertNotNull(nowPlayingPage.movies.get(0));
    }

    @Test
    public void test_get_genres() throws IOException {
        Call<Genres> callGenres = MovieDB.getInstance().moviesService().genres(MovieDB.API_KEY);
        Genres genres = callGenres.execute().body();
        Assert.assertNotNull(genres);
        Assert.assertNotNull(genres.genres);
    }

    @Test
    public void test_configuration() throws IOException {
        Call<Configuration> callConfig = MovieDB.getInstance().moviesService().configuration(MovieDB.API_KEY);
        Configuration config = callConfig.execute().body();
        Assert.assertNotNull(config);
        Assert.assertNotNull(config.images.posterSizes);
    }

    @Test
    public void test_search() throws IOException {
        Call<Page> callSearchPage = MovieDB.getInstance().moviesService().search(MovieDB.API_KEY, DataToTest.MOVIE_TITLE, 1);
        Page page = callSearchPage.execute().body();
        Assert.assertEquals(page.movies.get(0).id, DataToTest.MOVIE_ID);
    }

}