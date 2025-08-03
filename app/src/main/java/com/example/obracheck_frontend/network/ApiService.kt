package com.example.obracheck_frontend.network

import com.example.obracheck_frontend.model.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    // -------------------- SITES --------------------
    @GET("sites")
    suspend fun listSites(): List<SiteDto>

    @GET("sites/{id}")
    suspend fun getSiteById(@Path("id") id: Long): SiteDto

    @POST("sites")
    suspend fun createSite(@Body request: CreateSiteRequestDto): SiteDto

    @PUT("sites/{id}")
    suspend fun updateSite(@Path("id") id: Long, @Body request: CreateSiteRequestDto): SiteDto

    @DELETE("sites/{id}")
    suspend fun deleteSite(@Path("id") id: Long)

    // -------------------- USERS --------------------
    @GET("users")
    suspend fun listUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): UserDto

    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequestDto): UserDto

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: CreateUserRequestDto): UserDto

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)

    // -------------------- WORKERS --------------------
    @GET("workers")
    suspend fun listWorkers(): List<WorkerDto>

    @GET("workers/{id}")
    suspend fun getWorkerById(@Path("id") id: Long): WorkerDto

    @POST("workers")
    suspend fun createWorker(@Body request: CreateWorkerRequestDto): WorkerDto

    @PUT("workers/{id}")
    suspend fun updateWorker(@Path("id") id: Long, @Body request: CreateWorkerRequestDto): WorkerDto

    @DELETE("workers/{id}")
    suspend fun deleteWorker(@Path("id") id: Long)

    // -------------------- PROGRESSES --------------------
    @GET("progresses")
    suspend fun listProgresses(): List<ProgressDto>

    @GET("progresses/{id}")
    suspend fun getProgressById(@Path("id") id: Long): ProgressDto

    @POST("progresses")
    suspend fun createProgress(@Body request: CreateProgressRequestDto): ProgressDto

    @PUT("progresses/{id}")
    suspend fun updateProgress(@Path("id") id: Long, @Body request: CreateProgressRequestDto): ProgressDto

    @DELETE("progresses/{id}")
    suspend fun deleteProgress(@Path("id") id: Long)

    // -------------------- EVIDENCES --------------------
    @GET("evidences")
    suspend fun listEvidences(): EvidenceListResponseDto

    @GET("evidences/{id}")
    suspend fun getEvidenceById(@Path("id") id: Long): EvidenceDto

    @Multipart
    @POST("evidences/upload")
    suspend fun uploadEvidence(
        @Part file: MultipartBody.Part,
        @Part("progressId") progressId: Long
    ): EvidenceUploadResponseDto

    @Multipart
    @PUT("evidences/{id}")
    suspend fun updateEvidence(
        @Path("id") id: Long,
        @Part file: MultipartBody.Part
    ): EvidenceUploadResponseDto

    @GET("evidences/{id}/download")
    suspend fun downloadEvidence(@Path("id") id: Long): ByteArray

    @DELETE("evidences/{id}")
    suspend fun deleteEvidence(@Path("id") id: Long)
}
