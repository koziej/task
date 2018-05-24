package com.github.koziej.task.hotel.repositories

import com.github.koziej.task.booking.Booking
import com.github.koziej.task.booking.repositories.BookingRepository
import com.github.koziej.task.hotel.Hotel
import com.github.koziej.task.hotel.Room
import com.github.koziej.task.hotel.RoomSearchConditions
import com.github.koziej.task.user.User
import com.github.koziej.task.user.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.Month

@SpringBootTest
class RoomSearchRepositoryTest extends Specification {

    @Autowired
    HotelRepository hotelRepository

    @Autowired
    BookingRepository bookingRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    RoomSearchRepository searchRepository

    def 'room search returns expected result'() {
        given:
        String city = UUID.randomUUID().toString()
        Hotel hotel = Hotel.builder()
                .name(UUID.randomUUID().toString())
                .city(city)
                .build()
        Room room = Room.builder()
                .price(new BigDecimal('5'))
                .build()
        hotel.addRoom(room)
        hotelRepository.save(hotel)
        RoomSearchConditions conditions = RoomSearchConditions.builder()
                .priceFrom(BigDecimal.ZERO)
                .priceTo(BigDecimal.TEN)
                .dateFrom(LocalDate.now())
                .dateTo(LocalDate.now().plusDays(1))
                .city(city)
                .build()

        when:
        List<Room> rooms = searchRepository.search(conditions)

        then:
        rooms.size() == 1
        rooms.find { it.id == room.id }
    }

    @Unroll
    def 'room search returns empty list for city: #city and priceFrom: #priceFrom and priceTo: #priceTo and dateFrom: #dateFrom and dateTo: #dateTo'() {
        given:
        User user = userRepository.save(User.builder().name('Steven').build())
        Hotel hotel = Hotel.builder()
                .name(UUID.randomUUID().toString())
                .city('Dublin')
                .build()
        Room room = Room.builder()
                .price(new BigDecimal('5'))
                .build()
        hotel.addRoom(room)
        hotelRepository.save(hotel)
        bookingRepository.save(
                Booking.builder()
                        .user(user)
                        .room(room)
                        .dateFrom(LocalDate.of(2025, Month.JANUARY, 5))
                        .dateTo(LocalDate.of(2025, Month.JANUARY, 10))
                        .build())
        RoomSearchConditions conditions = RoomSearchConditions.builder()
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .city(city)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build()

        when:
        List<Room> rooms = searchRepository.search(conditions)

        then:
        rooms.isEmpty()

        where:
        city      | priceFrom             | priceTo               | dateFrom                             | dateTo
        'Glasgow' | BigDecimal.ZERO       | BigDecimal.ONE        | LocalDate.of(2025, Month.JANUARY, 1) | LocalDate.of(2025, Month.JANUARY, 5)
        'Dublin'  | new BigDecimal('5.5') | BigDecimal.TEN        | LocalDate.of(2025, Month.JANUARY, 1) | LocalDate.of(2025, Month.JANUARY, 5)
        'Dublin'  | BigDecimal.ONE        | new BigDecimal('4.5') | LocalDate.of(2025, Month.JANUARY, 1) | LocalDate.of(2025, Month.JANUARY, 5)
        'Dublin'  | BigDecimal.ONE        | BigDecimal.TEN        | LocalDate.of(2025, Month.JANUARY, 1) | LocalDate.of(2025, Month.JANUARY, 6)
        'Dublin'  | BigDecimal.ONE        | BigDecimal.TEN        | LocalDate.of(2025, Month.JANUARY, 9) | LocalDate.of(2025, Month.JANUARY, 15)
        'Dublin'  | BigDecimal.ONE        | BigDecimal.TEN        | LocalDate.of(2025, Month.JANUARY, 1) | LocalDate.of(2025, Month.JANUARY, 15)
        'Dublin'  | BigDecimal.ONE        | BigDecimal.TEN        | LocalDate.of(2025, Month.JANUARY, 7) | LocalDate.of(2025, Month.JANUARY, 8)
    }
}
