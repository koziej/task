package com.github.koziej.task.booking;

import com.github.koziej.task.booking.repositories.BookingRepository;
import com.github.koziej.task.hotel.Room;
import com.github.koziej.task.hotel.repositories.RoomRepository;
import com.github.koziej.task.user.User;
import com.github.koziej.task.user.repositories.UserRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.time.LocalDate;

@Service
public class BookingService {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Setter
    private Runnable beforeBooking = () -> {
    };

    @Transactional
    public Booking addBooking(LocalDate dateFrom, LocalDate dateTo, String userId, String roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(IllegalStateException::new);
        if (room.isBooked(dateFrom, dateTo)) {
            throw new IllegalStateException("Room not available for selected period.");
        }
        User user = userRepository.findOneById(userId);
        Booking booking = Booking.builder()
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .room(room)
                .user(user)
                .build();
        entityManager.lock(room, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        beforeBooking.run();
        return repository.save(booking);
    }
}
