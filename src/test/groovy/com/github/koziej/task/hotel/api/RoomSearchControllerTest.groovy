package com.github.koziej.task.hotel.api

import com.github.koziej.task.hotel.Hotel
import com.github.koziej.task.hotel.Room
import com.github.koziej.task.hotel.RoomSearchConditions
import com.github.koziej.task.hotel.repositories.RoomSearchRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RoomSearchControllerTest extends Specification {

    static String testCity = UUID.randomUUID().toString()

    static String testHotelId = UUID.randomUUID().toString()

    static String testFirstRoomId = UUID.randomUUID().toString()

    static String testSecondRoomId = UUID.randomUUID().toString()

    @Autowired
    TestRestTemplate restTemplate

    @TestConfiguration
    static class Config {
        @Bean
        RoomSearchRepository roomSearchRepository() {
            return new RoomSearchRepository() {
                @Override
                List<Room> search(RoomSearchConditions conditions) {
                    Hotel hotel = Hotel.builder()
                            .id(testHotelId)
                            .city(testCity)
                            .build()
                    Room room1 = Room.builder()
                            .id(testFirstRoomId)
                            .price(new BigDecimal('2'))
                            .build()
                    Room room2 = Room.builder()
                            .id(testSecondRoomId)
                            .price(new BigDecimal('3'))
                            .build()
                    hotel.addRoom(room1)
                    hotel.addRoom(room2)
                    return [room1, room2]
                }
            }
        }
    }

    def 'add returns 200 and expected body'() {
        given:
        String roomSearchUrl = UriComponentsBuilder.fromPath('/rooms')
                .queryParam('city', testCity)
                .queryParam('priceFrom', 1)
                .queryParam('priceTo', 4)
                .queryParam('dateFrom', '2025-01-01')
                .queryParam('dateTo', '2025-01-05')
                .toUriString()

        when:
        ResponseEntity<List<HotelSearchOutput>> responseEntity = restTemplate.exchange(
                roomSearchUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<HotelSearchOutput>>() {
                })
        List<HotelSearchOutput> outputList = responseEntity.getBody()
        HotelSearchOutput output = outputList[0]

        then:
        responseEntity.statusCode == HttpStatus.OK
        outputList.size() == 1
        output.hotelId == testHotelId
        output.rooms.size() == 2
        output.rooms.find { it.id == testFirstRoomId && it.price == 2 }
        output.rooms.find { it.id == testSecondRoomId && it.price == 3 }
    }
}
