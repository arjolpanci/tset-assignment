package com.arjol.assignment.controller

import com.arjol.assignment.dto.DeployRequest
import com.arjol.assignment.dto.ServiceResponse
import com.arjol.assignment.model.SystemVersion
import com.arjol.assignment.service.DeploymentService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DeploymentController(private val service: DeploymentService) {

    @PostMapping("/deploy")
    fun deploy(@RequestBody req: DeployRequest) = service.deploy(req.name, req.version)

    @GetMapping("/services")
    fun getServices(@RequestParam systemVersion: Int) =
        service.getServices(systemVersion).map { ServiceResponse(it.serviceName, it.serviceVersion) }
}

