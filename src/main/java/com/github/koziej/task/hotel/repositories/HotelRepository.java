package com.github.koziej.task.hotel.repositories;

import com.github.koziej.task.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, String> {
}
