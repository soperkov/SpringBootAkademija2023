package hr.kingict.springbootakademija2023.service;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;
import hr.kingict.springbootakademija2023.dto.FlightSearchResultDto;
import hr.kingict.springbootakademija2023.mapper.FlightOfferSearchFlightSearchResultDtoMapper;
import hr.kingict.springbootakademija2023.mapper.FlightSearchResultDtoFlightSearchResultEntityMapper;
import hr.kingict.springbootakademija2023.mapper.FlightSearchResultEntityFlightSearchResultDtoMapper;
import hr.kingict.springbootakademija2023.model.FlightSearchEntity;
import hr.kingict.springbootakademija2023.model.FlightSearchResultEntity;
import hr.kingict.springbootakademija2023.repository.FlightSearchEntityRepository;
import hr.kingict.springbootakademija2023.repository.FlightSearchResultEntityRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AmadeusService {

    Logger logger = LoggerFactory.getLogger(AmadeusService.class);

    @Autowired
    private FlightOfferSearchFlightSearchResultDtoMapper flightSearchResultDtoMapper;

    @Autowired
    private Amadeus amadeus;

    @Autowired
    private FlightSearchEntityRepository flightSearchEntityRepository;

    @Autowired
    private FlightSearchResultEntityRepository flightSearchResultEntityRepository;

    @Autowired
    private FlightSearchResultDtoFlightSearchResultEntityMapper flightSearchResultEntityMapper;

    @Autowired
    private FlightSearchResultEntityFlightSearchResultDtoMapper flightSearchResultEntityFlightSearchResultDtoMapper;
    public List<Location> searchAirports(String keyword) {

        try {

            Params param = Params
                    .with("subType", Locations.AIRPORT)
                    .and("keyword", keyword);
            return Arrays
                    .asList(amadeus.referenceData.locations
                            .get(param));
        } catch (Exception e){

            logger.error("Search airports error: ", e);
            return Collections.emptyList();

        }

    }

    @Transactional
    public List<FlightSearchResultDto> searchFlights(String originLocation,
                                                     String destinationLocation,
                                                     LocalDate departureDate,
                                                     LocalDate returnDate,
                                                     Integer adults){

        try {

            FlightSearchEntity existingFlightSearch = flightSearchEntityRepository
                    .findOneByOriginLocationCodeAndDestinationLocationCodeAndDepartureDateAndReturnDateAndAdults
                    (originLocation,
                            destinationLocation,
                            departureDate,
                            returnDate,
                            adults);

            if(existingFlightSearch != null){
                List<FlightSearchResultEntity> flightSearchResultEntityList = existingFlightSearch.getFlightSearchResultEntityList();

                logger.info("Dohvatio podatke iz baze.");

                return flightSearchResultEntityList
                        .stream()
                        .map(flightSearchResultEntity -> flightSearchResultEntityFlightSearchResultDtoMapper.map(flightSearchResultEntity))
                        .toList();

            }

            FlightSearchEntity flightSearchEntity = new FlightSearchEntity();
            flightSearchEntity.setOriginLocationCode(originLocation);
            flightSearchEntity.setDestinationLocationCode(destinationLocation);
            flightSearchEntity.setDepartureDate(departureDate);
            flightSearchEntity.setReturnDate(returnDate);
            flightSearchEntity.setAdults(adults);

            flightSearchEntity.setDateCreated(LocalDate.now());
            flightSearchEntity.setUserCreated("Sonja");

            flightSearchEntityRepository.save(flightSearchEntity);

            Params params = Params
                    .with("originLocationCode", originLocation)
                    .and("destinationLocationCode", destinationLocation)
                    .and("departureDate", departureDate.toString())
                    .and("adults", adults)
                    .and("nonStop", true)
                    .and("max", 5);


            if (returnDate != null){
                params.and("returnDate", returnDate.toString());
            }

            List<FlightOfferSearch> flightOfferSearchList = Arrays
                    .asList
                    (amadeus.shopping.flightOffersSearch
                            .get(params));

            List<FlightSearchResultDto> flightSearchResultDtoList = flightOfferSearchList
                    .stream()
                    .map(flightOfferSearch -> flightSearchResultDtoMapper.map(flightOfferSearch))
                    .toList();

            flightSearchResultDtoList
                    .stream()
                    .map(flightSearchResultDto -> flightSearchResultEntityMapper.map(flightSearchResultDto))
                    .forEach(flightSearchResultEntity -> {

                        flightSearchResultEntity.setFlightSearchEntity(flightSearchEntity);
                        flightSearchResultEntityRepository.save(flightSearchResultEntity);

                    });

            logger.warn("Dohvatio podatke iz Amadeusa. To će nas koštati.");

            return  flightSearchResultDtoList;


        } catch (Exception e) {
            logger.error("Search flights error: ",e);
            return Collections.emptyList();
        }

    }
}
