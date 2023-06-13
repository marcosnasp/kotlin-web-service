package com.example.kotlinwebservice

import jakarta.persistence.*
import jakarta.persistence.GenerationType.UUID
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 *  In Kotlin, if a class doesn't include any members (properties or functions), you can omit the class body ({}) for good.
 * */
/*
* @SpringBootApplication
* It enables Spring Boot's auto-configuration,
* component scan, and be able to define an extra configuration on their "application class".
* */
@SpringBootApplication
@EnableJpaRepositories
//@ComponentScan(basePackages = ["com.example.java"])
class KotlinWebServiceApplication

fun main(args: Array<String>) {
    runApplication<KotlinWebServiceApplication>(*args)
}

@RestController
class MessageController(val service: MessageService) {
    /*
    * Hello, $name! expression is called a String template in Kotlin.
    * String templates are String literals that contain embedded expressions.
    * This is a convenient replacement for String concatenation operations
    * */
    @GetMapping("/")
    fun index(): List<Message> = service.findMessages()

    @GetMapping("/{id}")
    fun messageById(@PathVariable id: String): List<Message> =
        service.findMessageById(id)

    /**
     * The method responsible for handling HTTP POST requests needs to be annotated
     * with @PostMapping annotation. To be able to convert the JSON sent as HTTP Body content into an object,
     * you need to use the @RequestBody annotation for the method argument.
     * Thanks to having Jackson library in the classpath of the application,
     * the conversion happens automatically.
     * */
    @PostMapping("/")
    fun post(@RequestBody message: Message) {
        service.save(message)
    }

}

/*
* The main purpose of data classes in Kotlin is to hold data.
* Such classes are marked with the data keyword,
* and some standard functionality and some utility functions
* are often mechanically derivable from the class structure.
* */
@Entity
@Table(name = "messages")
data class Message(@Id @GeneratedValue(strategy = UUID) val id: String?, val text: String)

// https://kotlinlang.org/docs/jvm-spring-boot-add-db-support.html#add-database-support
/**
 * A class in Kotlin can have a primary constructor and one or more secondary constructors.
 * The primary constructor is a part of the class header,
 * and it goes after the class name and optional type parameters.
 * In our case, the constructor is (val db: JdbcTemplate).
 */
@Service
class MessageService(val db: MessageRepository) {

    fun findMessages(): List<Message> = db.findAll().toList()

    fun save(message: Message) {
        db.save(message)
    }

    fun findMessageById(id: String): List<Message> = db.findById(id).toList()

    fun <T : Any> Optional<out T>.toList(): List<T> = if (isPresent) listOf(get()) else emptyList()
}

interface MessageRepository : CrudRepository<Message, String>