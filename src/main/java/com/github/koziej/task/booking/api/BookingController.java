package com.github.koziej.task.booking.api;

import com.github.koziej.task.booking.Booking;
import com.github.koziej.task.booking.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("bookings")
public class BookingController {

    @Autowired
    private BookingService service;

    @PostMapping
    public ResponseEntity<BookingOutput> addBooking(@Valid @RequestBody BookingInput input,
                                                    @RequestHeader("User-Id") String userId,
                                                    UriComponentsBuilder uriBuilder) {
        LocalDate dateFrom = input.getDateFrom();
        LocalDate dateTo = input.getDateTo();
        if (invalidPeriod(dateFrom, dateTo)) {
            return ResponseEntity.badRequest().build();
        }
        String roomId = input.getRoomId();
        Booking booking = service.addBooking(dateFrom, dateTo, userId, roomId);
        BookingOutput output = bookingToOutput(booking);
        UriComponents uriComponents = uriBuilder.path("bookings/{id}").buildAndExpand(output.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(output);
    }

    private boolean invalidPeriod(LocalDate dateFrom, LocalDate dateTo) {
        return !dateFrom.isBefore(dateTo) || dateFrom.isBefore(LocalDate.now());
    }

    private BookingOutput bookingToOutput(Booking booking) {
        return BookingOutput.builder()
                .id(booking.getId())
                .roomId(booking.getRoom().getId())
                .dateFrom(booking.getDateFrom())
                .dateTo(booking.getDateTo())
                .build();
    }
}
