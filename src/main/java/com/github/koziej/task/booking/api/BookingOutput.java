package com.github.koziej.task.booking.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingOutput {

    private String id;

    private String roomId;

    private LocalDate dateFrom;

    private LocalDate dateTo;
}
