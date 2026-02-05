package com.arjol.assignment

import com.arjol.assignment.dto.DeployRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class DeploymentIntegrationTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var mapper: ObjectMapper

    @Test
    fun test_example_deployment() {
        // Mocking the set of requests with the order given in the example in the email

        deploy("Service A", 1, 1)
        deploy("Service B", 1, 2)
        deploy("Service A", 2, 3)
        deploy("Service B", 1, 3)

        mockMvc.perform(get("/services?systemVersion=2"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[?(@.name == 'Service A' && @.version == 1)]").exists())
            .andExpect(jsonPath("$[?(@.name == 'Service B' && @.version == 1)]").exists())

        mockMvc.perform(get("/services?systemVersion=3"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[?(@.name == 'Service A' && @.version == 2)]").exists())
            .andExpect(jsonPath("$[?(@.name == 'Service B' && @.version == 1)]").exists())
    }


    private fun deploy(name: String, version: Int, expectedSystemVersion: Int) {
        val json = mapper.writeValueAsString(DeployRequest(name, version))
        mockMvc.perform(post("/deploy")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isOk)
            .andExpect(content().string(expectedSystemVersion.toString()))
    }
}