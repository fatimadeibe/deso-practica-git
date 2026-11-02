package repository.dao;

import domain.Reservation;
import exception.DataAccessException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;

public class ReservationExportDaoImpl implements ReservationExportDao {
  private final File file;

  public ReservationExportDaoImpl() {
    this.file = new File("reservations-export.csv");
  }

  // visible for testing, so tests can point at a temp file instead of the real one
  ReservationExportDaoImpl(File file) {
    this.file = file;
  }

  // Ejercicio 10 (Parte A)
  @Override
  public void exportAll(List<Reservation> reservations) throws DataAccessException {
    try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
      writer.write("reservationId,date,ownerUsername,serviceCompany,calculatedPrice");
      for (Reservation reservation : reservations) {
        writer.newLine();
        writer.write(
            String.join(
                ",",
                escapeCsvField(reservation.getId().toString()),
                escapeCsvField(reservation.getDate().toString()),
                escapeCsvField(reservation.getOwner().getUsername()),
                escapeCsvField(reservation.getService().getCompany()),
                String.format(Locale.ROOT, "%.2f", reservation.calculatePrice())));
      }
    } catch (IOException e) {
      throw new DataAccessException("Error al exportar reservas a CSV", e);
    }
  }

  private String escapeCsvField(String value) {
    if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
      return "\"" + value.replace("\"", "\"\"") + "\"";
    }
    return value;
  }
}
