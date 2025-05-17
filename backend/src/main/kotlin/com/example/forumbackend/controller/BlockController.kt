package com.example.forumbackend.controller

import com.example.forumbackend.dto.BlockDto
import com.example.forumbackend.model.block.BlockRequest
import com.example.forumbackend.security.JwtUtil
import com.example.forumbackend.service.BlockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts/{postId}/blocks")
class BlockController(
    private val blockService: BlockService,
    private val jwt: JwtUtil
) {

    @GetMapping
    fun list(@PathVariable postId: Long): ResponseEntity<List<BlockDto>> =
        ResponseEntity.ok(blockService.listByPost(postId))

    @PostMapping
    fun add(
        @PathVariable postId: Long,
        @RequestBody  req: BlockRequest,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<BlockDto> {
        val uid = jwt.extractUserId(auth.removePrefix("Bearer "))
        val dto = blockService.addBlock(postId, uid, req.content)
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @DeleteMapping("/{blockId}")
    fun delete(
        @PathVariable blockId: Long,
        @RequestHeader("Authorization") auth: String
    ): ResponseEntity<Void> {
        val uid = jwt.extractUserId(auth.removePrefix("Bearer "))
        blockService.deleteBlock(blockId, uid)
        return ResponseEntity.noContent().build()
    }
}