package com.github.koziej.task

import com.github.koziej.task.booking.api.BookingOutput
import com.github.koziej.task.booking.repositories.BookingRepository
import com.github.koziej.task.hotel.api.HotelOutput
import com.github.koziej.task.hotel.api.HotelSearchOutput
import com.github.koziej.task.hotel.api.RoomOutput
import com.github.koziej.task.hotel.repositories.HotelRepository
import com.github.koziej.task.user.api.UserOutput
import com.github.koziej.task.user.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.util.UriComponentsBuilder
import spock.lang.Specification

import java.time.LocalDate
import java.time.Month

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingFlowAcceptanceTest extends Specification {

    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    HotelRepository hotelRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    BookingRepository bookingRepository

    def 'user books a room'() {
        given: 'user add request'
        String userInput = '''
            {
                "name": "Steven"
            }
        '''
        HttpHeaders userAddHeaders = new HttpHeaders()
        userAddHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> userAddRequest = new HttpEntity<String>(userInput, userAddHeaders)

        when: 'called an add user endpoint'
        ResponseEntity<UserOutput> userAddResponse = restTemplate.postForEntity('/users', userAddRequest, UserOutput)
        UserOutput userOutput = userAddResponse.getBody()

        then: 'new user is created'
        userAddResponse.statusCode == HttpStatus.CREATED
        userAddResponse.headers.getLocation().toString().endsWith("/users/${userOutput.id}")
        userOutput.id
        userOutput.name == 'Steven'
        userRepository.findById(userOutput.id).isPresent()

        and: 'given hotel add request'
        String hotelName = 'Marriott'
        String city = 'Warsaw'
        String hotelInput = /
            {
                "name": "$hotelName",
                "city": "$city"
            }
        /
        HttpHeaders hotelAddHeaders = new HttpHeaders()
        hotelAddHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> hotelAddRequest = new HttpEntity<String>(hotelInput, hotelAddHeaders)

        when:
        ResponseEntity<HotelOutput> hotelAddResponse = restTemplate.postForEntity("/hotels", hotelAddRequest, HotelOutput)
        HotelOutput hotelOutput = hotelAddResponse.getBody()

        then:
        hotelAddResponse.statusCode == HttpStatus.CREATED
        hotelOutput.id
        hotelOutput.name == hotelName
        hotelOutput.city == city
        hotelAddResponse.headers.getLocation().toString().endsWith("/hotels/$hotelOutput.id")

        and: 'given room add request'
        BigDecimal price = BigDecimal.TEN
        String roomInput = /
            {
                "price": "$price"
            }
        /
        HttpHeaders roomAddHeaders = new HttpHeaders()
        roomAddHeaders.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<String> roomAddResponse = new HttpEntity<String>(roomInput, roomAddHeaders)

        when:
        ResponseEntity<RoomOutput> responseEntity = restTemplate.postForEntity("/hotels/{hotelId}/rooms", roomAddResponse, RoomOutput, hotelOutput.id)
        RoomOutput roomOutput = responseEntity.getBody()

        then:
        responseEntity.statusCode == HttpStatus.CREATED
        roomOutput.id
        roomOutput.price == price
        responseEntity.headers.getLocation().toString().endsWith("/hotels/$hotelOutput.id/rooms/$roomOutput.id")

        and: 'given room search url'
        LocalDate dateFrom = LocalDate.of(2025, Month.AUGUST, 1)
        LocalDate dateTo = dateFrom.plusDays(7)
        int priceFrom = 10
        int priceTo = 20
        String roomSearchUrl = UriComponentsBuilder.fromPath('/rooms')
                .queryParam('city', city)
                .queryParam('dateFrom', dateFrom)
                .queryParam("dateTo", dateTo)
                .queryParam("priceFrom", priceFrom)
                .queryParam("priceTo", priceTo)
                .toUriString()

        when: 'room search is performed'
        ResponseEntity<List<HotelSearchOutput>> roomSearchResponse = restTemplate.exchange(
                roomSearchUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<HotelSearchOutput>>() {
                })
        List<HotelSearchOutput> hotelRoomsOutputs = roomSearchResponse.getBody()

        then: 'room is returned'
        roomSearchResponse.statusCode == HttpStatus.OK
        hotelRoomsOutputs.size() == 1
        List<RoomOutput> rooms = hotelRoomsOutputs[0].rooms
        rooms.size() == 1
        RoomOutput foundRoomOutput = rooms[0]
        foundRoomOutput.id == roomOutput.id
        foundRoomOutput.price == price

        and: 'given add booking request'
        String bookingInput = /
            {
                "roomId": "${foundRoomOutput.id}",
                "dateFrom": "${dateFrom}",
                "dateTo": "${dateTo}"
            }
        /
        HttpHeaders bookingHeaders = new HttpHeaders()
        bookingHeaders.setContentType(MediaType.APPLICATION_JSON)
        bookingHeaders.add('User-Id', userOutput.id)
        HttpEntity<String> bookingAddRequest = new HttpEntity<String>(bookingInput, bookingHeaders)

        when: 'called a booking add endpoint'
        ResponseEntity<BookingOutput> bookingAddResponse = restTemplate.postForEntity('/bookings', bookingAddRequest, BookingOutput)
        BookingOutput bookingAddOutput = bookingAddResponse.getBody()

        then: 'room is booked'
        bookingAddResponse.statusCode == HttpStatus.CREATED
        bookingAddResponse.headers.getLocation().toString().endsWith("/bookings/${bookingAddOutput.id}")
        bookingAddOutput.id
        bookingAddOutput.roomId == foundRoomOutput.id
        bookingAddOutput.dateFrom == dateFrom
        bookingAddOutput.dateTo == dateTo
        bookingRepository.findById(bookingAddOutput.id).isPresent()
    }
}
