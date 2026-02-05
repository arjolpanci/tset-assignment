package com.arjol.assignment.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "system_version")
class SystemVersion(
    @Id
    val versionNumber: Int,
) {
    @OneToMany(mappedBy = "systemVersion",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    val services: MutableList<DeployedService> = mutableListOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SystemVersion) return false
        return versionNumber == other.versionNumber
    }

    override fun hashCode(): Int = versionNumber.hashCode()
}