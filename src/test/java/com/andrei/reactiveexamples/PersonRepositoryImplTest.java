package com.andrei.reactiveexamples;

import com.andrei.reactiveexamples.domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryImplTest {

    PersonRepository personRepository;

    @BeforeEach
    void setUp() throws Exception {
        personRepository = new PersonRepositoryImpl();
    }

    @Test
    void getByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);
        Person person = personMono.block();

        System.out.println(person.toString());
    }

    @Test
    void getByIdSubscribe() {
        Mono<Person> personMono = personRepository.getById(3);

        StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
        personMono.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void getByIdSubscribeNotFound() {
        Mono<Person> personMono = personRepository.getById(9);
        StepVerifier.create(personMono).expectNextCount(0).verifyComplete();
        personMono.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void getByIdMapFunction() {
        Mono<Person> personMono = personRepository.getById(1);
        personMono.map(person -> {
            System.out.println(person.toString());
            return person.getFirstName();
        }).subscribe(firstName -> System.out.println("from map "+ firstName));

    }


    @Test
    void findAllBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();
        final Person person = personFlux.blockFirst();
        System.out.println(person.toString());
    }

    @Test
    void findAllSubscribe() {
        Flux<Person> personFlux = personRepository.findAll();
        StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();
        personFlux.subscribe(person -> {
            System.out.println(person.toString());
        });
    }

    @Test
    void findAllListMono() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<List<Person>> personListMono = personFlux.collectList();

        personListMono.subscribe(list -> {
           list.forEach(System.out::println);
        });
    }

    @Test
    void testFindPersonById() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == 3).next();
        personMono.subscribe(person -> System.out.println(person.toString()));
    }


    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == 8).next();
        personMono.subscribe(person -> System.out.println(person.toString()));
    }


    @Test
    void testFindPersonByIdNotFoundWithException() {
        Flux<Person> personFlux = personRepository.findAll();
        Mono<Person> personMono = personFlux.filter(person -> person.getId() == 8).single();
        personMono.subscribe(person -> System.out.println(person.toString()));

        personMono.doOnError(person ->{
            System.out.println("Booom");
        }).onErrorReturn(Person.builder().build()).subscribe(person -> System.out.println(person.toString()));
    }
}
