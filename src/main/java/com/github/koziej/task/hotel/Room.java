package com.github.koziej.task.hotel;

import com.github.koziej.task.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @Min(0)
    @NotNull
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;

    @Version
    private int version;

    public boolean isBooked(LocalDate dateFrom, LocalDate dateTo) {
        return bookings.stream().anyMatch(booking ->
                (booking.getDateFrom().plusDays(1).isAfter(dateFrom) && booking.getDateFrom().isBefore(dateTo)) ||
                        (booking.getDateTo().isAfter(dateFrom) && booking.getDateTo().minusDays(1).isBefore(dateTo)) ||
                        (booking.getDateFrom().minusDays(1).isBefore(dateFrom) && booking.getDateTo().plusDays(1).isAfter(dateTo)));
    }
}
