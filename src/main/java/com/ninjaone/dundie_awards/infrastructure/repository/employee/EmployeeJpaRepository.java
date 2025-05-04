package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
interface EmployeeJpaRepository extends JpaRepository<Employee, Long> {

    @Modifying
    @Query(value = """
        UPDATE employees
        SET dundie_awards = COALESCE(dundie_awards, 0) + 1
        WHERE id = :employeeId
    """, nativeQuery = true)
    int giveDundie(@Param("employeeId") Long employeeId);

}
