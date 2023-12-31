package hr.kingict.springbootakademija2023.repository;

import hr.kingict.springbootakademija2023.model.FlightSearchResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightSearchResultEntityRepository extends JpaRepository<FlightSearchResultEntity, Integer> {
}
