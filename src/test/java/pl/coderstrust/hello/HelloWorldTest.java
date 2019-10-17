package pl.coderstrust.hello;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


class HelloWorldTest {
    @Test
    public void shouldReturnSomething() {
        assertEquals("Hello World", HelloWorld.getSomething());
    }

    @Test
    public void shouldReturnSomething2() {
        assertEquals("Hello World 2", HelloWorld.getSomethingElse());
    }

    @Test
    public void shouldReturnSomething3() {
        assertEquals("Hello World 3", HelloWorld.getSomethingElse3());
    }

    @Test
    public void shouldReturnSomething4() {
        assertEquals("Hello World 4", HelloWorld.getSomethingElse4());
    }

    @Test
    public void shouldReturnSomething5() {
        assertEquals("Hello World 5", HelloWorld.getSomethingElse5());
    }

    @Test
    public void shouldReturnSomething6() {
        assertEquals("Hello World 6", HelloWorld.getSomethingElse6());
    }

    @Test
    public void addTest() {
        HelloWorld hello = new HelloWorld();
        assertEquals(4, hello.add(2,2));
    }
}
