package repository.dao;

import domain.Flight;
import domain.Reservation;
import domain.TouristService;
import domain.User;
import exception.DataAccessException;
import exception.InvalidDateRangeException;
import service.TouristServiceService;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationDaoFileImpl implements ReservationDao {
  private static ReservationDaoFileImpl instance;
  private final File file;

  private ReservationDaoFileImpl() {
    this.file = new File("reservations.csv");
  }

  // visible for testing, so tests can point at a temp file instead of the real one
  ReservationDaoFileImpl(File file) {
    this.file = file;
  }

  // singleton
  public static synchronized ReservationDaoFileImpl getInstance() {
    if (instance == null) {
      instance = new ReservationDaoFileImpl();
    }

    return instance;
  }


  @Override
  public void save(Reservation reservation) throws DataAccessException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
      String line =
          String.format(
              "%s,%s,%s,%s\n",
              reservation.getId(),
              reservation.getService().getId(),
              reservation.getOwner().getId(),
              reservation.getDate());
      bw.write(line);
    } catch (IOException e) {
      throw new DataAccessException("Error al guardar la reserva", e);
    }
  }

  // Ejercicio 3 (Parte A)
  @Override
  public List<Reservation> findByDateRange(Instant from, Instant to) throws DataAccessException {
    List <Reservation> reservas= new ArrayList<>();
    try (BufferedReader reader= new BufferedReader(new FileReader(this.file))){
      String linea;
      while((linea = reader.readLine())!=null){
        Reservation reservacion= lineaAReservation(linea);
        reservas.add(reservacion);
      }
    }
    catch (IOException arc){
      throw new DataAccessException("Error al leer el archivo", arc);
    }

   return reservas.stream().filter(r-> ! r.getDate().isBefore(from) && !r.getDate().isAfter(to))
           .toList();

  }

  //cada linea del cvs tiene
  //971,     adcd482f-2b16-4062-8d37-d6d963e229f5,e5b88627-79da-4ca0-bc53-175bcb825710,2026-08-23T22:25:31.973505999Z.
  //idreserva| id servicio                        |id de usuario                        |fecha

  private Reservation lineaAReservation (String linea){

    String[] campos=linea.split(",");
    Long reservacionID= Long.parseLong(campos[0]);
    UUID serviceUUID=UUID.fromString(campos[1]);
    UUID userID= UUID.fromString(campos[2]);
    Instant fecha=Instant.parse(campos[3]);

    //para q la reserva sea un objeto válido
    User nuevoUsuario= new User();
    nuevoUsuario.setId(userID);
    //dejo q asigne un ID generico(? no tiene un setter con compania y servicio
    TouristService nuevoServicio= new Flight(null);

    return new Reservation.Builder().id(reservacionID).date(fecha).owner(nuevoUsuario).service(nuevoServicio).build();
  }

  }
