package ru.yandex.practikum.filmorate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FilmorateApplicationTests {

    @BeforeAll
    static void start() {
        String[] args = new String[0];
        FilmorateApplication.main(args);
    }

    @Test
    void contextLoads() {
    }

}
