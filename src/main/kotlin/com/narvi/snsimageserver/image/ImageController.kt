package com.narvi.snsimageserver.image

import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/images")
class ImageController(
    private val imageStorageService: ImageStorageService,
) {

    @PostMapping("/upload")
    fun uploadImage(
        @RequestParam("image") file: MultipartFile,
    ): ResponseEntity<String> {
        val imageId = imageStorageService.store(file)
        return ResponseEntity.ok(imageId)
    }

    @GetMapping("/view/{imageId}")
    fun getImage(
        @PathVariable("imageId")
        imageId: String,
        @RequestParam("thumbnail", defaultValue = "false")
        isThumbnail: Boolean
    ): ResponseEntity<Resource> = imageStorageService.get(imageId, isThumbnail)?.let {
        ResponseEntity.ok().contentType(MediaType.parseMediaType("image/jpeg")).body(it)
    } ?: ResponseEntity.notFound().build()
}