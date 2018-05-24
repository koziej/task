package com.github.koziej.task.hotel.repositories;

import com.github.koziej.task.booking.Booking;
import com.github.koziej.task.booking.Booking_;
import com.github.koziej.task.hotel.Hotel;
import com.github.koziej.task.hotel.Hotel_;
import com.github.koziej.task.hotel.Room;
import com.github.koziej.task.hotel.RoomSearchConditions;
import com.github.koziej.task.hotel.Room_;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.List;

@Repository
public class RoomSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Room> search(RoomSearchConditions conditions) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Room> query = builder.createQuery(Room.class);
        Root<Room> room = query.from(Room.class);
        Join<Room, Hotel> hotel = room.join(Room_.hotel);

        Subquery<String> subquery = query.subquery(String.class);
        Root<Booking> booking = subquery.from(Booking.class);
        subquery.select(booking.get(Booking_.id))
                .where(builder.and(
                        builder.equal(booking.get(Booking_.room).get(Room_.id), room.get(Room_.id)),
                        builder.or(
                                builder.and(
                                        builder.greaterThanOrEqualTo(booking.get(Booking_.dateFrom), conditions.getDateFrom()),
                                        builder.lessThan(booking.get(Booking_.dateFrom), conditions.getDateTo())
                                ),
                                builder.and(
                                        builder.greaterThan(booking.get(Booking_.dateTo), conditions.getDateFrom()),
                                        builder.lessThanOrEqualTo(booking.get(Booking_.dateTo), conditions.getDateTo())
                                ),
                                builder.and(
                                        builder.lessThanOrEqualTo(booking.get(Booking_.dateFrom), conditions.getDateFrom()),
                                        builder.greaterThanOrEqualTo(booking.get(Booking_.dateTo), conditions.getDateTo())
                                )
                        )
                ));

        Predicate whereCity = builder.equal(hotel.get(Hotel_.city), conditions.getCity());
        Predicate wherePrice = builder.between(room.get(Room_.price), conditions.getPriceFrom(), conditions.getPriceTo());
        Predicate whereDates = builder.not(builder.exists(subquery));

        query.select(room).where(whereCity, wherePrice, whereDates);

        return entityManager.createQuery(query).getResultList();
    }
}
