package com.github.koziej.task.hotel;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class RoomSearchConditions {

    private String city;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private BigDecimal priceFrom;

    private BigDecimal priceTo;
}
