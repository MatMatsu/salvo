package com.codeoftheweb.salvo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ShipTypeRepository extends JpaRepository<ShipType, Long> {
    ShipType findByType(@Param("type") String type);
}
