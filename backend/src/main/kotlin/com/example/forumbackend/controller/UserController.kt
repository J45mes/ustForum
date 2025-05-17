package com.example.forumbackend.controller

import com.example.forumbackend.entity.UserInfo
import com.example.forumbackend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserInfo>> {
        val users = userService.getAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserInfo> {
        return try {
            val user = userService.getUserById(id)
            ResponseEntity.ok(user)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserInfo> {
        return try {
            val user = userService.getUserByUsername(username)
            ResponseEntity.ok(user)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserInfo> {
        return try {
            val user = userService.getUserByEmail(email)
            ResponseEntity.ok(user)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createUser(@RequestBody user: UserInfo): ResponseEntity<UserInfo> {
        return try {
            val createdUser = userService.createUser(user)
            ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: UserInfo): ResponseEntity<UserInfo> {
        return try {
            val updatedUser = userService.updateUser(id, user)
            ResponseEntity.ok(updatedUser)
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            userService.deleteUser(id)
            ResponseEntity.noContent().build()
        } catch (e: NoSuchElementException) {
            ResponseEntity.notFound().build()
        }
    }
}