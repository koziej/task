package com.github.koziej.task.user.api;

import com.github.koziej.task.user.User;
import com.github.koziej.task.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<UserOutput> addUser(@Valid @RequestBody UserInput input,
                                              UriComponentsBuilder uriBuilder) {
        User user = service.addUser(inputToUser(input));
        UserOutput output = userToOutput(user);
        UriComponents uriComponents = uriBuilder.path("users/{id}").buildAndExpand(output.getId());
        return ResponseEntity.created(uriComponents.toUri()).body(output);
    }

    private static User inputToUser(UserInput input) {
        return User.builder()
                .name(input.getName())
                .build();
    }

    private static UserOutput userToOutput(User user) {
        return UserOutput.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
