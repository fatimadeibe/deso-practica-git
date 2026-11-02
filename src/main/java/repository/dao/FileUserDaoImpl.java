package repository.dao;

import domain.User;
import exception.DataAccessException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserDaoImpl implements FileUserDao {
  private final File file;

  public FileUserDaoImpl() {
    this.file = new File("users.txt");
  }

  // visible for testing, so tests can point at a temp file instead of the real one
  FileUserDaoImpl(File file) {
    this.file = file;
  }

  // Ejercicio 9 (Parte A)
  @Override
  public List<User> findAll() throws DataAccessException {
    List<User> users = new ArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] data = line.trim().split("\\s+");
        if (data.length != 2) {
          continue;
        }

        try {
          users.add(
              User.builder()
                  .id(UUID.fromString(data[0]))
                  .username(data[1])
                  .build());
        } catch (IllegalArgumentException e) {
          // A malformed UUID makes this line invalid, but should not stop the remaining reads.
        }
      }
    } catch (IOException e) {
      throw new DataAccessException("Error al leer usuarios", e);
    }
    return users;
  }
}
