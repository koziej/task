package com.github.koziej.task.booking.repositories;

import com.github.koziej.task.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, String> {

}
