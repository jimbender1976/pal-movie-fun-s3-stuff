package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;
    private final PlatformTransactionManager albumTraxManager;
    private final PlatformTransactionManager moviesTraxManager;

    public HomeController(
            MoviesBean moviesBean,
            AlbumsBean albumsBean,
            MovieFixtures movieFixtures,
            AlbumFixtures albumFixtures,
            @Qualifier("albumsPlatformTransactionManager") PlatformTransactionManager albumTraxManager,
            @Qualifier("moviesPlatformTransactionManager") PlatformTransactionManager moviesTraxManager
            ) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
        this.albumTraxManager = albumTraxManager;
        this.moviesTraxManager = moviesTraxManager;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        for (Movie movie : movieFixtures.load()) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus transactionStatus = moviesTraxManager.getTransaction(def);
            moviesBean.addMovie(movie);
            moviesTraxManager.commit(transactionStatus);
        }

        for (Album album : albumFixtures.load()) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus transactionStatus = albumTraxManager.getTransaction(def);
            albumsBean.addAlbum(album);
            albumTraxManager.commit(transactionStatus);
        }

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }
}
