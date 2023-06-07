package com.example.kotlinwebservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.SqlParameterValue
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 *  In Kotlin, if a class doesn't include any members (properties or functions), you can omit the class body ({}) for good.
 * */
/*
* @SpringBootApplication
* It enables Spring Boot's auto-configuration,
* component scan, and be able to define an extra configuration on their "application class".
* */
@SpringBootApplication
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
    fun messageById(@PathVariable id: String): Message? = service.findByMessageId(id)

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
data class Message(val id: String?, val text: String)

// https://kotlinlang.org/docs/jvm-spring-boot-add-db-support.html#add-database-support
/**
 * A class in Kotlin can have a primary constructor and one or more secondary constructors.
 * The primary constructor is a part of the class header,
 * and it goes after the class name and optional type parameters.
 * In our case, the constructor is (val db: JdbcTemplate).
 */
@Service
class MessageService(val db: JdbcTemplate) {
    fun findMessages(): List<Message> = db.query("select * from messages") { response, _ ->
        Message(
            response.getString("id"),
            response.getString("text")
        )
    }

    fun save(message: Message) {
        /**
         * The code message.id ?: UUID.randomUUID().toString()
         * uses the Elvis operator (if-not-null-else shorthand) ?:.
         * If the expression to the left of ?: is not null,
         * the Elvis operator returns it; otherwise, it returns the expression to the right.
         * Note that the expression on the right-hand side is evaluated only if the left-hand side is null.
         * */
        val id = message.id ?: UUID.randomUUID().toString()
        db.update("insert into messages values(?, ?)", id, message.text)
    }

    fun findByMessageId(id: String): Message? {
       val sql: String =  "select * from messages where id = ?"
       return db.queryForObject(sql, arrayOf(id)) { rs, _ ->
            Message(
                rs.getString("id"),
                rs.getString("text")
            )
        }
    }

}