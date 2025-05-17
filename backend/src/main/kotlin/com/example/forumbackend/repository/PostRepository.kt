package com.example.forumbackend.repository

import com.example.forumbackend.entity.Post
import com.example.forumbackend.entity.UserInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : JpaRepository<Post, Long> {
    fun findByAuthor(author: UserInfo): List<Post>

    // Changed from int to String to match entity type
    fun findByCategory(category: String): List<Post>

    fun findByIsPrivate(isPrivate: Boolean): List<Post>

    // Fix JPQL query syntax - LIMIT not directly supported in JPQL
    @Query("SELECT p FROM Post p ORDER BY p.views DESC")
    fun findTrendingPosts(): List<Post>

    // Alternative using method naming convention
    fun findTop5ByOrderByViewsDesc(): List<Post>

    @Query("""
    SELECT DISTINCT p.tag 
      FROM   Post p 
     WHERE   p.tag IS NOT NULL 
       AND   p.tag LIKE :pattern
  """)
    fun findDistinctTagsLike(@Param("pattern") pattern: String): List<String>
}