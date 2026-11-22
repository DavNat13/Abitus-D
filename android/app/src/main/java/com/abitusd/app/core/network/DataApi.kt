package com.abitusd.app.core.network

import com.abitusd.app.core.network.dto.CatalogDto
import com.abitusd.app.core.network.dto.PullResponse
import com.abitusd.app.core.network.dto.PushRequest
import com.abitusd.app.core.network.dto.PushResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** Catalog and sync endpoints. Authenticated; the client renews the token when needed. */
interface DataApi {
    @GET("v1/catalog")
    suspend fun catalog(
        @Query("since") since: Int,
    ): CatalogDto

    @POST("v1/sync/push")
    suspend fun push(
        @Body body: PushRequest,
    ): PushResponse

    @GET("v1/sync/pull")
    suspend fun pull(
        @Query("cursor") cursor: Long,
    ): PullResponse
}
