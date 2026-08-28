package br.com.renan.vinylcollection.data.network.api

import br.com.renan.vinylcollection.data.network.dto.DiscogsAddReleaseResponse
import br.com.renan.vinylcollection.data.network.dto.DiscogsIdentityResponse
import br.com.renan.vinylcollection.data.network.dto.DiscogsReleaseInstancesResponse
import br.com.renan.vinylcollection.data.network.dto.DiscogsSearchResponse
import retrofit2.Response
import retrofit2.http.*

interface DiscogsApiService {

    @GET("oauth/identity")
    suspend fun getUserIdentity(): Response<DiscogsIdentityResponse>

    @GET("database/search")
    suspend fun searchVinyls(
        @Query("q") query: String,
        @Query("type") type: String = "release"
    ): Response<DiscogsSearchResponse>

    @GET("users/{username}/collection/releases/{release_id}")
    suspend fun getReleaseInstances(
        @Path("username") username: String,
        @Path("release_id") releaseId: Int
    ): Response<DiscogsReleaseInstancesResponse>

    @POST("users/{username}/collection/folders/1/releases/{release_id}")
    suspend fun addReleaseToCollection(
        @Path("username") username: String,
        @Path("release_id") releaseId: Int
    ): Response<DiscogsAddReleaseResponse>

    @PUT("users/{username}/collection/folders/1/releases/{release_id}/instances/{instance_id}")
    suspend fun updateReleaseCondition(
        @Path("username") username: String,
        @Path("release_id") releaseId: Int,
        @Path("instance_id") instanceId: Long,
        @Body conditionData: Map<String, String>
    ): Response<Unit>

    @DELETE("users/{username}/collection/folders/1/releases/{release_id}/instances/{instance_id}")
    suspend fun removeReleaseFromCollection(
        @Path("username") username: String,
        @Path("release_id") releaseId: Int,
        @Path("instance_id") instanceId: Long
    ): Response<Unit>
}