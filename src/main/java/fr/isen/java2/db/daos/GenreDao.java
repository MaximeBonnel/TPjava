package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {
	// Function to list all genres
	public List<Genre> listGenres() {
		List<Genre> listOfGenres = new ArrayList<>();

        try (Connection connection = DataSourceFactory.getDataSource().getConnection();){
			try (Statement statement = connection.createStatement()) {
				try (ResultSet resultSet = statement.executeQuery("SELECT * FROM genre");) {
					while(resultSet.next()) {
						Genre genre = new Genre(
								resultSet.getInt("idgenre"),
								resultSet.getString("name")
						);
						listOfGenres.add(genre);
					}
					// Return the list of genres
					return listOfGenres;
				} catch (SQLException e) {
					throw new RuntimeException("Error while listing genres", e);
				}
			} catch (SQLException e) {
                throw new RuntimeException("Error creating the statement", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to the database", e);
		}
	}

	// Function to get a genre by name
	public Genre getGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();){
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM genre WHERE name = ?");){
				statement.setString(1, name);
				try (ResultSet resultSet = statement.executeQuery();) {
					if (resultSet.next()) {
						// Return the genre
						return new Genre(resultSet.getInt("idgenre"),
								resultSet.getString("name"));
					}
					// Return null if the genre is not found
                    return null;
				} catch (SQLException e) {
                    throw new RuntimeException("Error while listing genres", e);
                }
			} catch (SQLException e) {
                throw new RuntimeException("Error creating the statement", e);
            }
		} catch (SQLException e) {
            throw new RuntimeException("Cannot connect to the database", e);
		}
	}

	// Function to add a genre
	public void addGenre(String name) {
		try (Connection connection = DataSourceFactory.getDataSource().getConnection();){
			String sqlQuery = "INSERT INTO genre(name) VALUES(?)";
			try (PreparedStatement statement = connection.prepareStatement(sqlQuery);){
				statement.setString(1, name);
				statement.executeUpdate();
			} catch (SQLException e) {
                throw new RuntimeException("Error creating the statement", e);
            }
		} catch (SQLException e) {
			throw new RuntimeException("Cannot connect to the database", e);
		}
	}
}
