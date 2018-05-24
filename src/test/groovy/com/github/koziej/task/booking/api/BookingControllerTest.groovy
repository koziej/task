package com.github.koziej.task.booking.api

import com.github.koziej.task.booking.Booking
import com.github.koziej.task.booking.BookingService
import com.github.koziej.task.hotel.Room
import com.github.koziej.task.user.User
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

import java.time.LocalDate
import java.time.Month

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTest extends Specification {

    static String testUserId = UUID.randomUUID().toString()

    static String testRoomId = UUID.randomUUID().toString()

    static String testBookingId = UUID.randomUUID().toString()

    @Autowired
    TestRestTemplate restTemplate

    @TestConfiguration
    static class Config {
        @Bean
        BookingService bookingService() {
            return new BookingService() {
                @Override
                Booking addBooking(LocalDate dateFrom, LocalDate dateTo, String userId, String roomId) {
                    if (userId != testUserId || roomId != testRoomId) {
                        throw new IllegalArgumentException()
                    }
                    return Booking.builder()
                            .id(testBookingId)
                            .user(User.builder().id(userId).build())
                            .room(Room.builder().id(roomId).build())
                            .dateFrom(dateFrom)
                            .dateTo(dateTo)
                            .build()
                }
            }
        }
    }

    def 'add returns 201, resource location and expected body'() {
        given:
        String input = /
            {
                "roomId": "$testRoomId",
                "dateFrom": "2025-01-01",
                "dateTo": "2025-01-05"
            }
        /
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.add('User-Id', testUserId)
        HttpEntity<String> requestEntity = new HttpEntity<String>(input, headers)

        when:
        ResponseEntity<BookingOutput> responseEntity = restTemplate.postForEntity("/bookings", requestEntity, BookingOutput)
        BookingOutput output = responseEntity.getBody()

        then:
        responseEntity.statusCode == HttpStatus.CREATED
        output.id == testBookingId
        output.roomId == testRoomId
        output.dateFrom == LocalDate.of(2025, Month.JANUARY, 1)
        output.dateTo == LocalDate.of(2025, Month.JANUARY, 5)
        responseEntity.headers.getLocation().toString().endsWith("/bookings/$testBookingId")
    }

    @Unroll
    def 'add returns 400 when #description'() {
        given:
        def input = [
                "roomId"  : roomId,
                "dateFrom": dateFrom,
                "dateTo"  : dateTo
        ]

        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.add('User-Id', testUserId)
        HttpEntity<String> requestEntity = new HttpEntity<String>(new JsonBuilder(input).toPrettyString(), headers)

        when:
        ResponseEntity responseEntity = restTemplate.postForEntity("/bookings", requestEntity, Object)

        then:
        responseEntity.statusCode == HttpStatus.BAD_REQUEST

        where:
        description              | roomId     | dateFrom     | dateTo
        'empty roomId'           | ''         | '2025-01-01' | '2025-01-05'
        'no roomId'              | null       | '2025-01-01' | '2025-01-05'
        'empty dateFrom'         | testRoomId | ''           | '2025-01-05'
        'no dateFrom'            | testRoomId | null         | '2025-01-05'
        'empty dateTo'           | testRoomId | '2025-01-01' | ''
        'no dateTo'              | testRoomId | '2025-01-01' | null
        'dateFrom after dateTo'  | testRoomId | '2025-01-05' | '2025-01-01'
        'dateFrom equals dateTo' | testRoomId | '2025-01-05' | '2025-01-05'
        'dateFrom in the past'   | testRoomId | '2000-01-01' | '2025-01-05'
    }
}
