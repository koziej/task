package com.github.koziej.task.hotel

import com.github.koziej.task.booking.Booking
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class RoomTest extends Specification {

    @Unroll
    def 'isBooked returns false for period #description existing booking'() {
        given:
        Room room = Room.builder()
                .bookings([Booking.builder()
                                   .dateFrom(LocalDate.of(2025, 1, 10))
                                   .dateTo(LocalDate.of(2025, 1, 20))
                                   .build()])
                .build()

        expect:
        !room.isBooked(dateFrom, dateTo)

        where:
        description            | dateFrom                  | dateTo
        'ending a day before'  | LocalDate.of(2025, 1, 5)  | LocalDate.of(2025, 1, 9)
        'ending right before'  | LocalDate.of(2025, 1, 5)  | LocalDate.of(2025, 1, 10)
        'starting a day after' | LocalDate.of(2025, 1, 21) | LocalDate.of(2025, 1, 25)
        'starting right after' | LocalDate.of(2025, 1, 20) | LocalDate.of(2025, 1, 25)
    }

    @Unroll
    def 'isBooked returns true for period #description existing booking'() {
        given:
        Room room = Room.builder()
                .bookings([Booking.builder()
                                   .dateFrom(LocalDate.of(2025, 1, 10))
                                   .dateTo(LocalDate.of(2025, 1, 20))
                                   .build()])
                .build()

        expect:
        room.isBooked(dateFrom, dateTo)

        where:
        description       | dateFrom                  | dateTo
        'ending within'   | LocalDate.of(2025, 1, 5)  | LocalDate.of(2025, 1, 15)
        'starting within' | LocalDate.of(2025, 1, 15) | LocalDate.of(2025, 1, 25)
        'inside'          | LocalDate.of(2025, 1, 11) | LocalDate.of(2025, 1, 19)
        'the same as'     | LocalDate.of(2025, 1, 10) | LocalDate.of(2025, 1, 20)
        'containing'      | LocalDate.of(2025, 1, 5)  | LocalDate.of(2025, 1, 25)
    }
}
