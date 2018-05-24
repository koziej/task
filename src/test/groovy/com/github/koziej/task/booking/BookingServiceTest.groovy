package com.github.koziej.task.booking

import com.github.koziej.task.booking.repositories.BookingRepository
import com.github.koziej.task.hotel.Hotel
import com.github.koziej.task.hotel.Room
import com.github.koziej.task.hotel.repositories.HotelRepository
import com.github.koziej.task.user.User
import com.github.koziej.task.user.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import java.time.LocalDate

@SpringBootTest
class BookingServiceTest extends Specification {

    @Autowired
    BookingService service

    @Autowired
    BookingRepository repository

    @Autowired
    UserRepository userRepository

    @Autowired
    HotelRepository hotelRepository

    def 'new booking is added'() {
        given:
        User user = userRepository.save(User.builder().name('Steven').build())
        Hotel hotel = Hotel.builder()
                .name(UUID.randomUUID().toString())
                .city(UUID.randomUUID().toString())
                .build()
        Room room = Room.builder().price(BigDecimal.ONE).build()
        hotel.addRoom(room)
        hotelRepository.save(hotel)
        LocalDate dateFrom = LocalDate.now()
        LocalDate dateTo = LocalDate.now().plusDays(5)

        when:
        Booking booking = service.addBooking(
                dateFrom,
                dateTo,
                user.id,
                room.id)

        then:
        booking.id
        booking.dateFrom == dateFrom
        booking.dateTo == dateTo
        booking.user.id == user.id
        booking.room.id == room.id
    }

    def 'exception is thrown if room is already booked'() {
        given:
        User user = userRepository.save(User.builder().name('Steven').build())
        Hotel hotel = Hotel.builder()
                .name(UUID.randomUUID().toString())
                .city(UUID.randomUUID().toString())
                .build()
        Room room = Room.builder().hotel(hotel).price(BigDecimal.ONE).build()
        hotel.addRoom(room)
        hotelRepository.save(hotel)
        LocalDate dateFrom = LocalDate.now()
        LocalDate dateTo = LocalDate.now().plusDays(5)
        repository.save(Booking.builder()
                .user(user)
                .room(room)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build())

        when:
        service.addBooking(
                dateFrom,
                dateTo,
                user.id,
                room.id)

        then:
        thrown(IllegalStateException)
    }

    def 'exception is thrown if room id is incorrect'() {
        given:
        LocalDate dateFrom = LocalDate.now()
        LocalDate dateTo = LocalDate.now().plusDays(5)
        String userId = UUID.randomUUID().toString()
        String roomId = UUID.randomUUID().toString()

        when:
        service.addBooking(
                dateFrom,
                dateTo,
                userId,
                roomId)

        then:
        thrown(IllegalStateException)
    }
}
