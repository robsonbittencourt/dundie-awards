package com.ninjaone.dundie_awards.infrastructure.repository.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface EmployeeJpaRepository extends JpaRepository<Employee, Long> {

    @Query(value = """
        SELECT COALESCE(SUM(dundie_awards), 0)
        FROM employees
    """, nativeQuery = true)
    long totalDundies();

    @Modifying
    @Query(value = """
        UPDATE employees
        SET dundie_awards = COALESCE(dundie_awards, 0) + 1
        WHERE organization_id = :organizationId
            AND id >= :startId
            AND id <= :endId
    """, nativeQuery = true)
    void giveDundie(@Param("organizationId") Long organizationId, @Param("startId") Long startId, @Param("endId") Long endId);

    @Query(value = """
        WITH numbered_employees AS (
            SELECT id, ROW_NUMBER() OVER (ORDER BY id) AS row_num
            FROM employees
            WHERE organization_id = :organizationId
        )
        SELECT
            MIN(id) AS startEmployeeId,
            MAX(id) AS endEmployeeId
        FROM numbered_employees
        GROUP BY FLOOR((row_num - 1) / :chunkSize)
        ORDER BY startEmployeeId
    """, nativeQuery = true)
    List<EmployeeIds> findChunksOfEmployees(@Param("organizationId") Long organizationId, @Param("chunkSize") int chunkSize);

}
