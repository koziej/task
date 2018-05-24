package com.github.koziej.task.user

import com.github.koziej.task.user.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class UserServiceTest extends Specification {

    @Autowired
    private UserService service

    @Autowired
    private UserRepository repository

    def 'user is added'() {
        given:
        String name = 'Steven'
        User user = User.builder()
                .name(name)
                .build()

        when:
        User persistedUser = service.addUser(user)

        then:
        persistedUser.id
        persistedUser.name == name
        repository.findById(persistedUser.id).isPresent()
    }
}
