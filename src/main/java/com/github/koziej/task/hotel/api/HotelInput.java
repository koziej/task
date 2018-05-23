package com.github.koziej.task.hotel.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelInput {

    @NotBlank
    private String name;

    @NotBlank
    private String city;
}
