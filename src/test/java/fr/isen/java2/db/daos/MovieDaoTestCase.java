package fr.isen.java2.db.daos;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

public class MovieDaoTestCase {

	private MovieDao movieDao = new MovieDao();

	@Before
	public void initDb() throws Exception {
		Connection connection = DataSourceFactory.getDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS genre (idgenre INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , name VARCHAR(50) NOT NULL);");
		stmt.executeUpdate(
				"CREATE TABLE IF NOT EXISTS movie (\r\n"
				+ "  idmovie INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\r\n" + "  title VARCHAR(100) NOT NULL,\r\n"
				+ "  release_date DATETIME NULL,\r\n" + "  genre_id INT NOT NULL,\r\n" + "  duration INT NULL,\r\n"
				+ "  director VARCHAR(100) NOT NULL,\r\n" + "  summary MEDIUMTEXT NULL,\r\n"
				+ "  CONSTRAINT genre_fk FOREIGN KEY (genre_id) REFERENCES genre (idgenre));");
		stmt.executeUpdate("DELETE FROM movie");
		stmt.executeUpdate("DELETE FROM genre");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (1,'Drama')");
		stmt.executeUpdate("INSERT INTO genre(idgenre,name) VALUES (2,'Comedy')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (1, 'Title 1', '2015-11-26 12:00:00.000', 1, 120, 'director 1', 'summary of the first movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (2, 'My Title 2', '2015-11-14 12:00:00.000', 2, 114, 'director 2', 'summary of the second movie')");
		stmt.executeUpdate("INSERT INTO movie(idmovie,title, release_date, genre_id, duration, director, summary) "
				+ "VALUES (3, 'Third title', '2015-12-12 12:00:00.000', 2, 176, 'director 3', 'summary of the third movie')");
		stmt.close();
		connection.close();
	}
	
	 @Test
	 public void shouldListMovies() {
		 // WHEN
		 List<Movie> movies = movieDao.listMovies();
		 // THEN
		 assertThat(movies).hasSize(3);
		 assertThat(movies).extracting("id", "title", "genre.id").containsOnly(
				 tuple(1, "Title 1", 1),
				 tuple(2, "My Title 2",	2),
				 tuple(3, "Third title", 2)
		 );
	 }
	
	 @Test
	 public void shouldListMoviesByGenre() {
		 // WHEN
		 List<Movie> movies = movieDao.listMoviesByGenre("Comedy");
		 // THEN
		 assertThat(movies).hasSize(2);
		 assertThat(movies).extracting("id", "title", "genre.id").containsOnly(
				 tuple(2, "My Title 2", 2),
				 tuple(3, "Third title", 2)
		 );
	 }
	
	 @Test
	 public void shouldAddMovie() throws Exception {
		 // GIVEN
		 Movie movie = new Movie(null, "New Movie Title", LocalDate.of(2023, 12, 12), new Genre(1, "Drama"), 120, "New director", "New summary");
		 // WHEN
		 Movie addedMovie = movieDao.addMovie(movie);
		 // THEN
		 Connection connection = DataSourceFactory.getDataSource().getConnection();
		 Statement statement = connection.createStatement();
		 ResultSet resultSet = statement.executeQuery("SELECT * FROM movie WHERE title='New Movie Title'");
		 assertThat(resultSet.next()).isTrue();
		 assertThat(resultSet.getInt("idmovie")).isNotNull();
		 assertThat(addedMovie.getId()).isEqualTo(resultSet.getInt("idmovie"));
		 assertThat(resultSet.getString("title")).isEqualTo("New Movie Title");
		 assertThat(resultSet.getString("director")).isEqualTo("New director");
		 assertThat(resultSet.getInt("genre_id")).isEqualTo(1);
		 assertThat(resultSet.next()).isFalse();
		 resultSet.close();
		 statement.close();
		 connection.close();
	 }
}
