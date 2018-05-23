package com.github.koziej.task.hotel

import com.github.koziej.task.hotel.repositories.HotelRepository
import com.github.koziej.task.hotel.repositories.RoomRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class HotelServiceTest extends Specification {

    @Autowired
    private HotelService service

    @Autowired
    private HotelRepository hotelRepository

    @Autowired
    private RoomRepository roomRepository

    def 'hotel is added'() {
        given:
        String name = UUID.randomUUID().toString()
        String city = UUID.randomUUID().toString()
        Hotel hotel = Hotel.builder()
                .name(name)
                .city(city)
                .build()

        when:
        Hotel persistedHotel = service.addHotel(hotel)

        then:
        persistedHotel.id
        persistedHotel.name == hotel.name
        persistedHotel.city == hotel.city
        hotelRepository.findById(persistedHotel.id).isPresent()
    }

    def 'room is added'() {
        given:
        Hotel hotel = Hotel.builder()
                .name(UUID.randomUUID().toString())
                .city(UUID.randomUUID().toString())
                .build()
        hotelRepository.save(hotel)
        BigDecimal price = BigDecimal.TEN
        Room room = Room.builder()
                .price(price)
                .build()

        when:
        Room persistedRoom = service.addRoom(hotel.id, room)

        then:
        persistedRoom.id
        persistedRoom.price == room.price
        persistedRoom.hotel.id == hotel.id
        roomRepository.findById(room.id).isPresent()
    }

    def 'exception is thrown on add room when hotel not found'() {
        given:
        String hotelId = UUID.randomUUID().toString()
        Room room = Room.builder()
                .price(BigDecimal.TEN)
                .build()

        when:
        service.addRoom(hotelId, room)

        then:
        thrown(IllegalArgumentException)
    }
}
