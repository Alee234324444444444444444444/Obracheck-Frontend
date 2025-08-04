package com.example.obracheck_frontend.network

import com.example.obracheck_frontend.model.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // -------------------- SITES --------------------
    @GET("api/sites")
    suspend fun listSites(): List<SiteDto>

    @GET("api/sites/{id}")
    suspend fun getSiteById(@Path("id") id: Long): SiteDto

    @POST("api/sites")
    suspend fun createSite(@Body request: CreateSiteRequestDto): SiteDto

    @PUT("api/sites/{id}")
    suspend fun updateSite(@Path("id") id: Long, @Body request: CreateSiteRequestDto): SiteDto

    @DELETE("api/sites/{id}")
    suspend fun deleteSite(@Path("id") id: Long)

    // -------------------- USERS --------------------
    @GET("api/users")
    suspend fun listUsers(): List<UserDto>

    @POST("api/users")
    suspend fun createUser(@Body request: CreateUserRequestDto): UserDto

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: CreateUserRequestDto): UserDto

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    // -------------------- WORKERS --------------------
    @GET("api/workers")
    suspend fun listWorkers(): List<WorkerDto>

    @GET("api/workers/{id}")
    suspend fun getWorkerById(@Path("id") id: Long): WorkerDto

    @POST("api/workers")
    suspend fun createWorker(@Body request: CreateWorkerRequestDto): WorkerDto

    @PUT("api/workers/{id}")
    suspend fun updateWorker(@Path("id") id: Long, @Body request: CreateWorkerRequestDto): WorkerDto

    @DELETE("api/workers/{id}")
    suspend fun deleteWorker(@Path("id") id: Long)

    // -------------------- PROGRESSES --------------------
    @GET("api/progresses")
    suspend fun listProgresses(): List<ProgressDto>

    @GET("api/progresses/{id}")
    suspend fun getProgressById(@Path("id") id: Long): ProgressDto

    @POST("api/progresses")
    suspend fun createProgress(@Body request: CreateProgressRequestDto): ProgressDto

    @PUT("api/progresses/{id}")
    suspend fun updateProgress(@Path("id") id: Long, @Body request: CreateProgressRequestDto): ProgressDto

    @DELETE("api/progresses/{id}")
    suspend fun deleteProgress(@Path("id") id: Long)

    // -------------------- EVIDENCES --------------------
    @GET("api/evidences")
    suspend fun listEvidences(): EvidenceListResponseDto

    @GET("api/evidences/{id}")
    suspend fun getEvidenceById(@Path("id") id: Long): EvidenceDto

    @Multipart
    @POST("api/evidences/upload")
    suspend fun uploadEvidence(
        @Part file: MultipartBody.Part,
        @Part("progressId") progressId: Long
    ): EvidenceUploadResponseDto

    @Multipart
    @PUT("api/evidences/{id}")
    suspend fun updateEvidence(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): EvidenceUploadResponseDto

    @GET("api/evidences/{id}/download")
    suspend fun downloadEvidence(@Path("id") id: Long): ByteArray

    @DELETE("api/evidences/{id}")
    suspend fun deleteEvidence(@Path("id") id: Long)
}
