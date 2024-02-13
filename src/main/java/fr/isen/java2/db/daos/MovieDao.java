package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	// Function to list all movies
	public List<Movie> listMovies() {
		List<Movie> listOfMovies = new ArrayList<>();

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();){
			try (Statement statement = connection.createStatement()) {
				try (ResultSet resultSet = statement.executeQuery("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre");) {
					while(resultSet.next()) {
						Genre genre = new Genre(
								resultSet.getInt("idgenre"),
								resultSet.getString("name")
						);

						Movie movie = new Movie(
								resultSet.getInt("idmovie"),
								resultSet.getString("title"),
								resultSet.getDate("release_date").toLocalDate(),
								genre,
								resultSet.getInt("duration"),
								resultSet.getString("director"),
								resultSet.getString("summary")
						);
						listOfMovies.add(movie);
					}
					// Return the list of movies
					return listOfMovies;
				} catch (SQLException e) {
					throw new RuntimeException("Error while listing movies", e);
				}
			} catch (SQLException e) {
				throw new RuntimeException("Error creating the statement", e);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect to the database", e);
		}
	}

	// Function to list movies by genre
	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> listOfMovies = new ArrayList<>();

		try (Connection connection = DataSourceFactory.getDataSource().getConnection();){
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name = ?")) {
				statement.setString(1, genreName);
				try (ResultSet resultSet = statement.executeQuery();) {
					while(resultSet.next()) {
						Genre genre = new Genre(
								resultSet.getInt("idgenre"),
								resultSet.getString("name")
						);

						Movie movie = new Movie(
								resultSet.getInt("idmovie"),
								resultSet.getString("title"),
								resultSet.getDate("release_date").toLocalDate(),
								genre,
								resultSet.getInt("duration"),
								resultSet.getString("director"),
								resultSet.getString("summary")
						);
						listOfMovies.add(movie);
					}
					// Return the list of movies by genre
					return listOfMovies;
				} catch (SQLException e) {
					throw new RuntimeException("Error while listing movies", e);
				}
			} catch (SQLException e) {
				throw new RuntimeException("Error creating the statement", e);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect to the database", e);
		}
	}

	// Function to add a movie
	public Movie addMovie(Movie movie) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();){
			String sqlQuery = "INSERT INTO movie(title, release_date, genre_id, duration, director, summary) VALUES (?, ?, ?, ?, ?, ?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);) {
				statement.setString(1, movie.getTitle());
				statement.setString(2, movie.getReleaseDate().toString());
				statement.setInt(3, movie.getGenre().getId());
				statement.setInt(4, movie.getDuration());
				statement.setString(5, movie.getDirector());
				statement.setString(6, movie.getSummary());
				statement.executeUpdate();

				ResultSet ids = statement.getGeneratedKeys();
				if (ids.next()) {
					// Return the movie added
					return new Movie(ids.getInt(1), movie.getTitle(), movie.getReleaseDate(), movie.getGenre(), movie.getDuration(), movie.getDirector(), movie.getSummary());
				} else {
					throw new RuntimeException("Error while adding movie");
				}
			} catch (SQLException e) {
				throw new RuntimeException("Error creating the statement", e);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect to the database", e);
		}
	}
}
