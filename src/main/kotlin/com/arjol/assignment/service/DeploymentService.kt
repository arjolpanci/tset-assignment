package com.arjol.assignment.service

import com.arjol.assignment.model.DeployedService
import com.arjol.assignment.model.SystemVersion
import com.arjol.assignment.repository.DeployedServiceRepository
import com.arjol.assignment.repository.SystemVersionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeploymentService (
    private val systemRepo: SystemVersionRepository,
    private val serviceRepo : DeployedServiceRepository
) {

    @Transactional
    fun deploy(name: String, version: Int): Int {

        // This creates a row so pessimistic locking always works
        if (systemRepo.count() == 0L) {
            systemRepo.save(SystemVersion(0));
        }

        // Lock and read current system version (avoid race conditions)
        val latestSystemVersionEntry = systemRepo.findLatestWithLock()
            ?: throw IllegalStateException("SystemVersion must exist at this point");

        val currentVersionNumber = latestSystemVersionEntry.versionNumber;

        // Get current snapshot
        val currentSnapshot = serviceRepo.findAllBySystemVersion_VersionNumber(currentVersionNumber);
        val existingEntry = currentSnapshot.find { it.serviceName == name }

        // Check if anything changed
        if (existingEntry != null && existingEntry.serviceVersion == version) {
            return currentVersionNumber
        }

        val nextVersionNumber = currentVersionNumber + 1;
        val nextSystemVersion = SystemVersion(versionNumber = nextVersionNumber);

        // New system version gets the previous snapshot
        val nextSnapshot = currentSnapshot
            .filter { it.serviceName != name }
            .map {
                DeployedService(
                    systemVersion = nextSystemVersion,
                    serviceName = it.serviceName,
                    serviceVersion = it.serviceVersion,
                )
            }
            .toMutableList()

        // Update the service that triggered this change
        nextSnapshot.add(
            DeployedService(
                systemVersion = nextSystemVersion,
                serviceName = name,
                serviceVersion = version
            )
        )

        systemRepo.save(nextSystemVersion)
        serviceRepo.saveAll(nextSnapshot)

        return nextVersionNumber;
    }

    fun getServices(systemVersion: Int): List<DeployedService> = serviceRepo.findAllBySystemVersion_VersionNumber(systemVersion)

}