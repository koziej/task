package com.github.koziej.task.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Hotel {

    @Id
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @NotBlank
    private String name;

    @NotBlank
    private String city;

    @Builder.Default
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<Room> rooms = new LinkedList<>();

    public void addRoom(Room room) {
        room.setHotel(this);
        rooms.add(room);
    }
}
