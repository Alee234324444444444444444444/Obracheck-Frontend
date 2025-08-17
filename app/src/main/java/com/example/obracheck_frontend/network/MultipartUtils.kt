package com.example.obracheck_frontend.network

import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Locale

object MultipartUtils {

    fun filePartFromFile(file: File): MultipartBody.Part {
        val mime = guessMimeByName(file.name) ?: "image/jpeg"
        val body = file.asRequestBody(mime.toMediaType())
        val name = ensureExt(file.name, mime)
        return MultipartBody.Part.createFormData("file_name", name, body)
    }

    fun filePartFromBytes(fileName: String, bytes: ByteArray): MultipartBody.Part {
        val mime = guessMimeByName(fileName) ?: "image/jpeg"
        val body = bytes.toRequestBody(mime.toMediaType())
        val name = ensureExt(fileName, mime)
        return MultipartBody.Part.createFormData("file_name", name, body)
    }

    fun progressIdBody(progressId: Long): RequestBody =
        progressId.toString().toRequestBody("text/plain".toMediaType())

    private fun guessMimeByName(name: String): String? {
        val ext = name.substringAfterLast('.', "").lowercase(Locale.ROOT)
        if (ext.isEmpty()) return null
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
            ?: when (ext) {
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "webp" -> "image/webp"
                "gif" -> "image/gif"
                else -> null
            }
    }

    private fun ensureExt(name: String, mime: String): String {
        val want = when (mime) {
            "image/jpeg" -> "jpg"
            "image/png"  -> "png"
            "image/webp" -> "webp"
            "image/gif"  -> "gif"
            else -> null
        } ?: return name
        val base = name.substringBeforeLast('.', name)
        val ext  = name.substringAfterLast('.', "")
        return if (ext.equals(want, true)) name else "$base.$want"
    }
}
