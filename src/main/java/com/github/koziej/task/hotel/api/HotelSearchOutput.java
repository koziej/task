package com.github.koziej.task.hotel.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelSearchOutput {

    private String hotelId;

    private List<RoomOutput> rooms;
}
