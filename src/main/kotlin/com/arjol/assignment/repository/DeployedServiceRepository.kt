package com.arjol.assignment.repository

import com.arjol.assignment.model.DeployedService
import org.springframework.data.jpa.repository.JpaRepository

interface DeployedServiceRepository : JpaRepository<DeployedService, Long> {
    fun findAllBySystemVersion_VersionNumber(versionNumber: Int): List<DeployedService>
}