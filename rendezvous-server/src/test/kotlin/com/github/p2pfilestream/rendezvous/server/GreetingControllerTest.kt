package com.github.p2pfilestream.rendezvous.server


import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Integration test for Greeting Controller.
 *
 * https://spring.io/guides/tutorials/spring-boot-kotlin/
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GreetingControllerTest(@Autowired val restTemplate: TestRestTemplate) {

    @Test
    fun `Assert greeter response, status code and content-type`() {
        val entity = restTemplate.getForEntity<String>("/greeting")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.headers.getFirst("Content-Type")).contains("application/json")
        assertThat(entity.body).isEqualToIgnoringWhitespace(
            """
            {
              "id": 1,
              "content": "Hello, World"
            }
            """
        )
    }
}