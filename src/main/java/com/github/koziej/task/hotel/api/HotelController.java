package com.github.koziej.task.hotel.api;

import com.github.koziej.task.hotel.Hotel;
import com.github.koziej.task.hotel.HotelService;
import com.github.koziej.task.hotel.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;


@RestController
@RequestMapping("hotels")
public class HotelController {

    @Autowired
    private HotelService service;

    @PostMapping
    public ResponseEntity<HotelOutput> addHotel(@Valid @RequestBody HotelInput input,
                                                UriComponentsBuilder uriBuilder) {
        Hotel hotel = service.addHotel(inputToHotel(input));
        HotelOutput output = hotelToOutput(hotel);
        UriComponents uriComponents = uriBuilder.path("hotels/{hotelId}").buildAndExpand(output.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(output);
    }

    @PostMapping("{hotelId}/rooms")
    public ResponseEntity<RoomOutput> addRoom(@Valid @RequestBody RoomInput input,
                                              @PathVariable("hotelId") String hotelId,
                                              UriComponentsBuilder uriBuilder) {
        Room room = service.addRoom(hotelId, inputToRoom(input));
        RoomOutput output = roomToOutput(room);
        UriComponents uriComponents = uriBuilder.path("hotels/{hotelId}/rooms/{roomId}").buildAndExpand(hotelId, output.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(output);
    }

    private Hotel inputToHotel(HotelInput input) {
        return Hotel.builder()
                .name(input.getName())
                .city(input.getCity())
                .build();
    }

    private HotelOutput hotelToOutput(Hotel hotel) {
        return HotelOutput.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .build();
    }

    private Room inputToRoom(RoomInput input) {
        return Room.builder()
                .price(input.getPrice())
                .build();
    }

    private RoomOutput roomToOutput(Room room) {
        return RoomOutput.builder()
                .id(room.getId())
                .price(room.getPrice())
                .build();
    }
}
