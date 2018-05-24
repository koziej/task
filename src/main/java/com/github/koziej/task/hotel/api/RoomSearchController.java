package com.github.koziej.task.hotel.api;

import com.github.koziej.task.hotel.Hotel;
import com.github.koziej.task.hotel.Room;
import com.github.koziej.task.hotel.RoomSearchConditions;
import com.github.koziej.task.hotel.repositories.RoomSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("rooms")
public class RoomSearchController {

    @Autowired
    private RoomSearchRepository repository;

    @GetMapping
    public List<HotelSearchOutput> search(@RequestParam("city") String city,
                                          @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                          @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                          @RequestParam("priceFrom") BigDecimal priceFrom,
                                          @RequestParam("priceTo") BigDecimal priceTo) {
        RoomSearchConditions conditions = RoomSearchConditions.builder()
                .city(city)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .build();
        List<Room> rooms = repository.search(conditions);
        return roomsToOutput(rooms);
    }

    private List<HotelSearchOutput> roomsToOutput(List<Room> rooms) {
        Map<Hotel, List<Room>> hotelRooms = new HashMap<>();
        rooms.forEach(room -> {
            Hotel hotel = room.getHotel();
            hotelRooms.computeIfAbsent(hotel, k -> new ArrayList<>());
            hotelRooms.get(hotel).add(room);
        });
        return hotelRooms.entrySet().stream().map(e -> {
            List<RoomOutput> roomOutputs = e.getValue().stream()
                    .map(room -> RoomOutput.builder()
                            .id(room.getId())
                            .price(room.getPrice())
                            .build()).collect(Collectors.toList());
            return HotelSearchOutput.builder()
                    .hotelId(e.getKey().getId())
                    .rooms(roomOutputs)
                    .build();
        }).collect(Collectors.toList());
    }
}
