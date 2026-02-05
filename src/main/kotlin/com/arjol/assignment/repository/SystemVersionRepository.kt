package com.arjol.assignment.repository

import com.arjol.assignment.model.SystemVersion
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface SystemVersionRepository : JpaRepository<SystemVersion, Int> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SystemVersion s WHERE s.versionNumber = (SELECT MAX(v.versionNumber) FROM SystemVersion v)")
    fun findLatestWithLock(): SystemVersion?
}