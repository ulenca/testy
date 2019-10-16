package pl.coderstrust.hello;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldTest {

    @Test
    public void shouldReturnSomething() {
        assertEquals("Hello World", HelloWorld.getSomething());
    }
}