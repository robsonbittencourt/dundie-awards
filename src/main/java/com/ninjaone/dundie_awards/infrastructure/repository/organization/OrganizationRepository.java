package com.ninjaone.dundie_awards.infrastructure.repository.organization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationRepository {

    @Autowired
    private OrganizationJpaRepository jpaRepository;

    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

}
