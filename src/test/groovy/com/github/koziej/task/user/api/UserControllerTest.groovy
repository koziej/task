package com.github.koziej.task.user.api

import com.github.koziej.task.user.User
import com.github.koziej.task.user.UserService
import groovy.json.JsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Unroll

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest extends Specification {

    static String testUserId = UUID.randomUUID().toString()

    @Autowired
    TestRestTemplate restTemplate

    @TestConfiguration
    static class Config {
        @Bean
        UserService userService() {
            new UserService() {
                @Override
                User addUser(User user) {
                    return user.toBuilder().id(testUserId).build()
                }
            }
        }
    }

    def 'add user returns 201, resource location and expected body'() {
        given:
        String name = UUID.randomUUID().toString()
        String input = /
            {
                "name": "$name"
            }
        /
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> requestEntity = new HttpEntity<String>(input, headers)

        when:
        ResponseEntity<UserOutput> responseEntity = restTemplate.postForEntity("/users", requestEntity, UserOutput)
        UserOutput output = responseEntity.getBody()

        then:
        responseEntity.statusCode == HttpStatus.CREATED
        output.id == testUserId
        output.name == name
        responseEntity.headers.getLocation().toString().endsWith("/users/$testUserId")
    }

    @Unroll
    def 'add user returns 400 when #description'() {
        given:
        def input = [
                "name": name
        ]

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> requestEntity = new HttpEntity<String>(new JsonBuilder(input).toPrettyString(), headers)

        when:
        ResponseEntity responseEntity = restTemplate.postForEntity("/users", requestEntity, Object)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST

        where:
        description  | name | city
        'empty name' | ''   | 'x'
        'no name'    | null | 'x'
    }
}
