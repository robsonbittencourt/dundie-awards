package com.ninjaone.dundie_awards.infrastructure.repository.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface OrganizationJpaRepository extends JpaRepository<Organization, Long> {
}
