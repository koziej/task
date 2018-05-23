package com.github.koziej.task.hotel.api

import com.github.koziej.task.hotel.Hotel
import com.github.koziej.task.hotel.HotelService
import com.github.koziej.task.hotel.Room
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
class HotelControllerTest extends Specification {

    static String testHotelId = UUID.randomUUID().toString()

    static String testRoomId = UUID.randomUUID().toString()

    @Autowired
    TestRestTemplate restTemplate

    @TestConfiguration
    static class Config {
        @Bean
        HotelService hotelService() {
            new HotelService() {
                @Override
                Hotel addHotel(Hotel hotel) {
                    return hotel.toBuilder().id(testHotelId).build()
                }

                @Override
                Room addRoom(String hotelId, Room room) {
                    if (hotelId != testHotelId) {
                        throw new IllegalArgumentException()
                    }
                    return room.toBuilder().id(testRoomId).build()
                }
            }
        }
    }

    def 'add hotel returns 201, resource location and expected body'() {
        given:
        String name = UUID.randomUUID().toString()
        String city = UUID.randomUUID().toString()
        String input = /
            {
                "name": "$name",
                "city": "$city"
            }
        /
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> requestEntity = new HttpEntity<String>(input, headers)

        when:
        ResponseEntity<HotelOutput> responseEntity = restTemplate.postForEntity("/hotels", requestEntity, HotelOutput)
        HotelOutput output = responseEntity.getBody()

        then:
        responseEntity.statusCode == HttpStatus.CREATED
        output.id == testHotelId
        output.name == name
        output.city == city
        responseEntity.headers.getLocation().toString().endsWith("/hotels/$testHotelId")
    }

    @Unroll
    def 'add hotel returns 400 when #description'() {
        given:
        def input = [
                "name": name,
                "city": city
        ]

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> requestEntity = new HttpEntity<String>(new JsonBuilder(input).toPrettyString(), headers)

        when:
        ResponseEntity responseEntity = restTemplate.postForEntity("/hotels", requestEntity, Object)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST

        where:
        description  | name | city
        'empty name' | ''   | 'x'
        'no name'    | null | 'x'
        'empty city' | 'x'  | ''
        'no city'    | 'x'  | null
    }

    def 'add room returns 201, resource location and expected body'() {
        given:
        BigDecimal price = BigDecimal.TEN
        String input = /
            {
                "price": "$price"
            }
        /
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> requestEntity = new HttpEntity<String>(input, headers)

        when:
        ResponseEntity<RoomOutput> responseEntity = restTemplate.postForEntity("/hotels/{hotelId}/rooms", requestEntity, RoomOutput, testHotelId)
        RoomOutput output = responseEntity.getBody()

        then:
        responseEntity.statusCode == HttpStatus.CREATED
        output.id == testRoomId
        output.price == price
        responseEntity.headers.getLocation().toString().endsWith("/hotels/$testHotelId/rooms/$testRoomId")
    }

    @Unroll
    def 'add room returns 400 when #description'() {
        given:
        def input = [
                "price": price
        ]

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> requestEntity = new HttpEntity<String>(new JsonBuilder(input).toPrettyString(), headers)

        when:
        ResponseEntity responseEntity = restTemplate.postForEntity("/hotels", requestEntity, Object)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST

        where:
        description      | price
        'no price'       | null
        'zero price'     | BigDecimal.ZERO
        'negative price' | new BigDecimal(-1)
    }
}
