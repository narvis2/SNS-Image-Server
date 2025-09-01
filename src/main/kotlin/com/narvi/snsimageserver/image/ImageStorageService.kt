package com.narvi.snsimageserver.image

import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.geometry.Positions
import net.coobird.thumbnailator.name.Rename
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

@Service
class ImageStorageService(
    @Value("\${images.location}")
    private val imageRoot: String,
) {
    fun store(file: MultipartFile): String {
        try {
            val imageId = UUID.randomUUID().toString()
            val original = ImageIO.read(file.inputStream)
            val imageFile = File("${imageRoot}/${imageId}.jpg")
            // 원본
            Thumbnails.of(original).scale(1.0).outputFormat("jpg").toFile(imageFile)
            // Jpeg 로 변환된 이미지 파일 기준으로 썸네일 파일 생성
            Thumbnails.of(imageFile).crop(Positions.CENTER).size(500, 500).outputFormat("jpg").toFiles(Rename.SUFFIX_HYPHEN_THUMBNAIL)

            return imageId
        } catch (e: IOException) {
            throw RuntimeException("failed image store ${e.message}")
        }
    }

    fun get(imageId: String, isThumbnail: Boolean): Resource? {
        // imageId: abcd-1234
        // images/abcd-1234.jpg <-- 원본 파일
        // images/abcd-1234-thumbnail.jpg <-- 리사이즈 된 파일
        val file = Paths.get(imageRoot).resolve(imageId + (if (isThumbnail) "-thumbnail" else "") + ".jpg")
        return try {
            val resource = UrlResource(file.toUri())
            if (resource.exists() || resource.isReadable) {
                resource
            } else {
                null
            }
        } catch (ex: MalformedURLException) {
            println("⚠️ failed to load image $imageId, ex: ${ex.message}")
            null
        }
    }
}