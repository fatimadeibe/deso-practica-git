package service;

import domain.Reservation;
import domain.TouristService;
import domain.User;
import dto.reservation.CreateReservationRequest;
import dto.reservation.ReservationResponse;
import dto.reservation.ValidationResultDTO;
import dto.user.UserSummaryDTO;
import exception.DataAccessException;
import exception.DuplicateReservationException;
import exception.FlightNotFoundException;
import exception.InvalidDateRangeException;
import exception.UserNotFoundException;
import repository.dao.*;
import util.Pair;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ReservationService {
  private final ReservationDao reservationDao;
  private final UserDao userDao;
  private final FlightDao flightDao;

  public ReservationService(ReservationDao reservationDao, UserDao userDao, FlightDao flightDao) {
    this.reservationDao = reservationDao;
    this.userDao = userDao;
    this.flightDao = flightDao;
  }

  public ReservationResponse createReservation(CreateReservationRequest request)
      throws DataAccessException, UserNotFoundException, FlightNotFoundException {
    // 1. Resolver entidades de dominio desde los datos del DTO
    User user =
        userDao
            .findByUsername(request.username())
            .orElseThrow(() -> new UserNotFoundException(request.username()));
    TouristService service =
        flightDao
            .findByNumber(request.flightNumber())
            .orElseThrow(() -> new FlightNotFoundException(request.flightNumber()));

    // 2. Construir la reserva (lógica de dominio)
    Reservation reservation =
        new Reservation.Builder()
            .id((long) (Math.random() * 1000))
            .owner(user)
            .service(service)
            .date(request.date())
            .build();

    reservationDao.save(reservation);

    // 3. Mapear a DTO de respuesta
    return new ReservationResponse(
        reservation.getId(),
        user.getUsername(),
        service.toString(),
        reservation.calculatePrice(),
        request.date());
  }

  // Ejercicio 1
  public List<Reservation> getReservationsByUser(
      List<Reservation> reservations, String username, UserDao userDao)
      throws UserNotFoundException, DataAccessException {

      Optional <User> user= userDao.findByUsername(username);
      if(user.isEmpty()){
          throw new UserNotFoundException(username);
      }

    //creo un flujo q va pasando objetos del tipo Reservation
    //Stream<Reservation> stream= reservations.stream();
      return reservations.stream().filter(r->r.getOwner().getUsername().equals(username))
              .sorted(Comparator.comparingDouble(Reservation::calculatePrice).reversed()).toList();

  }

  // Ejercicio 3
  public List<Reservation> getTopNMostExpensive(
      ReservationDao reservationDao, Instant from, Instant to, int n)
      throws InvalidDateRangeException, DataAccessException {

    if (from == null || to == null || to.isBefore(from) ){
      throw new InvalidDateRangeException("Error en el rango definido");
    }
    if (n<=0) {
      return List.of(); //devuelve lista vacia si n menor a cero
    }
    List<Reservation> NMostExpensive= reservationDao.findByDateRange(from,to);
      return NMostExpensive.stream().sorted(Comparator.comparingDouble(Reservation::calculatePrice).reversed()).limit(n).toList();
  }


  // Ejercicio 5
  public Map<String, List<Reservation>> groupReservationsByCountry(List<Reservation> reservations) {
    throw new UnsupportedOperationException("Ejercicio 5: implement groupReservationsByCountry");
  }

  // Ejercicio 7
  public Reservation createReservation(
      String username,
      String flightNumber,
      Instant date,
      UserDao userDao,
      FlightDao flightDao,
      ReservationDao reservationDao)
      throws UserNotFoundException,
          FlightNotFoundException,
          InvalidDateRangeException,
          DataAccessException {
    User user =
        userDao
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));
    var service =
        flightDao
            .findByNumber(flightNumber)
            .orElseThrow(() -> new FlightNotFoundException(flightNumber));

    if (date == null || date.isBefore(Instant.now())) {
      throw new InvalidDateRangeException("Reservation date must not be null or in the past");
    }

    Reservation reservation =
        new Reservation.Builder()
            .id((long) (Math.random() * 1000))
            .owner(user)
            .service(service)
            .date(date)
            .build();
    try {
      reservationDao.save(reservation);
    } catch (DataAccessException e) {
      throw new DataAccessException(
          "Error al guardar la reserva para usuario "
              + username
              + " y vuelo "
              + flightNumber,
          e);
    }
    return reservation;
  }

  // Ejercicio 8
  public Reservation addReservationIfNotDuplicate(
      List<Reservation> existingReservations,
      Reservation newReservation,
      ReservationDao reservationDao)
      throws DuplicateReservationException, DataAccessException {
    boolean duplicate =
        existingReservations.stream()
            .anyMatch(
                reservation ->
                    Objects.equals(
                            reservation.getOwner().getId(), newReservation.getOwner().getId())
                        && Objects.equals(
                            reservation.getService().getId(), newReservation.getService().getId()));
    if (duplicate) {
      throw new DuplicateReservationException(
          newReservation.getOwner().getId(), newReservation.getService().getId());
    }
    reservationDao.save(newReservation);
    return newReservation;
  }

  // Ejercicio 10
  public void exportReservationsToCsv(
      List<Reservation> reservations, ReservationExportDao exportDao) throws DataAccessException {
    exportDao.exportAll(reservations);
  }

  // Ejercicio 11
  public List<UserSummaryDTO> getUserReservationSummaries(
      List<Reservation> reservations, UserDao userDao)
      throws UserNotFoundException, DataAccessException {
    throw new UnsupportedOperationException("Ejercicio 11: implement getUserReservationSummaries");
  }

  // Ejercicio 12
  public ValidationResultDTO validateReservationCandidates(
      List<Pair<String, String>> candidates, UserDao userDao, FlightDao flightDao)
      throws DataAccessException {
    throw new UnsupportedOperationException(
        "Ejercicio 12: implement validateReservationCandidates");
  }
}
