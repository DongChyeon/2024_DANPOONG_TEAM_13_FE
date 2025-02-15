package com.daon.onjung.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.math.BigInteger
import java.security.SecureRandom

fun fileFromContentUri(context: Context, contentUri: Uri): File {
    val fileExtension = getFileExtension(context, contentUri)
    val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""

    val tempFile = File(context.cacheDir, fileName)
    tempFile.createNewFile()

    try {
        val oStream = FileOutputStream(tempFile)
        val inputStream = context.contentResolver.openInputStream(contentUri)

        inputStream?.let {
            copy(inputStream, oStream)
        }

        oStream.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return tempFile
}

fun getFileExtension(context: Context, uri: Uri): String? {
    val fileType: String? = context.contentResolver.getType(uri)
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
}

@Throws(IOException::class)
private fun copy(source: InputStream, target: OutputStream) {
    val buf = ByteArray(8192)
    var length: Int
    while (source.read(buf).also { length = it } > 0) {
        target.write(buf, 0, length)
    }
}

fun resizeAndSaveImage(context: Context, originalFile: File): File {
    try {
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)

        val exif = ExifInterface(originalFile.absolutePath)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()
        when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val outputFile = createFile(context)
        outputFile.createNewFile()

        val outputStream = FileOutputStream(outputFile)

        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.flush()
        outputStream.close()

        return outputFile
    } catch (e: IOException) {
        e.printStackTrace()
        return originalFile
    }
}

fun saveImageWithoutCompressionOrResizing(context: Context, originalFile: File): File {
    return try {
        // EXIF 데이터를 읽어 회전 정보를 확인
        val exif = ExifInterface(originalFile.absolutePath)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        // 원본 파일 그대로 복사
        val outputFile = createFile(context)
        originalFile.copyTo(outputFile, overwrite = true)

        // 이미지 회전 정보 수정
        if (rotation != ExifInterface.ORIENTATION_NORMAL) {
            val updatedExif = ExifInterface(outputFile.absolutePath)
            updatedExif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
            updatedExif.saveAttributes()
        }

        outputFile
    } catch (e: IOException) {
        e.printStackTrace()
        originalFile
    }
}


private fun createFile(context: Context): File {
    val fileName = generateRandomFileName(10) // 10자리의 임의 파일명 생성

    val storageDir = context.cacheDir

    return File(storageDir, "${fileName}.jpg")
}

private fun generateRandomFileName(length: Int): String {
    val random = SecureRandom()
    val randomBytes = ByteArray(length / 2)
    random.nextBytes(randomBytes)
    val bi = BigInteger(1, randomBytes)
    return String.format("%0${length}x", bi)
}