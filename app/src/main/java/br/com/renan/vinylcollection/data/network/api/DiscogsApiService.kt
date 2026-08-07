package br.com.renan.vinylcollection.data.network.api

import br.com.renan.vinylcollection.data.network.dto.DiscogsSearchResponse
import retrofit2.Response
import retrofit2.http.*

interface DiscogsApiService {

    // GET: Pesquisar álbuns e artistas
    @GET("database/search")
    suspend fun searchVinyls(
        @Query("q") query: String,
        @Query("type") type: String = "release"
    ): Response<DiscogsSearchResponse>

    // POST: Adicionar um disco à coleção no servidor
    @POST("users/{username}/collection/folders/1/releases/{release_id}")
    suspend fun addReleaseToCollection(
        @Path("username") username: String,
        @Path("release_id") releaseId: Int
    ): Response<Unit>

    // PUT: Alterar os dados ou condição do disco na coleção do usuário
    @PUT("users/{username}/collection/folders/1/releases/{release_id}/instances/{instance_id}")
    suspend fun updateReleaseCondition(
        @Path("username") username: String,
        @Path("release_id") releaseId: Int,
        @Path("instance_id") instanceId: Int,
        @Body conditionData: Map<String, String>
    ): Response<Unit>

    // DELETE: Remover um item da lista do servidor
    @DELETE("users/{username}/collection/folders/1/releases/{release_id}/instances/{instance_id}")
    suspend fun removeReleaseFromCollection(
        @Path("username") username: String,
        @Path("release_id") releaseId: Int,
        @Path("instance_id") instanceId: Int
    ): Response<Unit>
}