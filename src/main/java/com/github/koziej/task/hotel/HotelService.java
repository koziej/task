package com.github.koziej.task.hotel;

import com.github.koziej.task.hotel.repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public Hotel addHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    @Transactional
    public Room addRoom(String hotelId, Room room) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(IllegalArgumentException::new);
        hotel.addRoom(room);
        hotelRepository.save(hotel);
        return room;
    }
}
