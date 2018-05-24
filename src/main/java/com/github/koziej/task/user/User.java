package com.github.koziej.task.user;

import com.github.koziej.task.booking.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}
