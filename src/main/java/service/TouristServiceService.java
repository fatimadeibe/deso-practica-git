package service;

import domain.Flight;
import domain.TouristService;
import exception.FlightNotFoundException;
import exception.ServiceNotFoundException;
import repository.dao.FlightDao;

import java.util.List;

public class TouristServiceService {

  // Ejercicio 2
  public double getAveragePriceByCompany(List<TouristService> services, String company) throws ServiceNotFoundException {
    /* Debe devolver el precio promedio (`service.calculatePrice()`) de los servicios de una compañía dada,
    con comparación case-insensitive. Si ningún servicio pertenece a esa compañía, debe lanzarse `ServiceNotFoundException`. */
    double suma, promedio;
    suma = promedio = 0;

    // Filtramos la lista comparando el servicio pasado como argumento (case insensitive)
    List<TouristService> companyServices = services.stream()
    .filter(service -> service.getCompany()
      .toLowerCase()
      .contains(company.toLowerCase())
    )
    .toList();

    // Si la lista está vacía, no hay servicios asociados al servicio dado
    if(companyServices.isEmpty()) throw new ServiceNotFoundException(company);

    for(TouristService service : companyServices) {
      suma += service.calculatePrice();
    }

    // Si la suma no es negativa o nula
    if(suma > 0) {
      promedio = suma / (double) (companyServices.size());
    }

    return promedio;
  }

  // Ejercicio 4
  public List<Flight> getFlightsSortedByPriceThenAirline(
      List<String> flightNumbers, FlightDao flightDao) throws FlightNotFoundException {
    throw new UnsupportedOperationException(
        "Ejercicio 4: implement getFlightsSortedByPriceThenAirline");
  }
}
