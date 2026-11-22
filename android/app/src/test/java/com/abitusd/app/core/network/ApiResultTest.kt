package com.abitusd.app.core.network

import com.abitusd.app.core.network.dto.AcceptedDto
import com.abitusd.app.core.network.dto.EmailRequest
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class ApiResultTest {
    private val server = MockWebServer()
    private lateinit var api: PublicAuthApi

    @Before
    fun setUp() {
        server.start()
        api =
            Retrofit
                .Builder()
                .baseUrl(server.url("/"))
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(PublicAuthApi::class.java)
    }

    @After
    fun tearDown() {
        server.close()
    }

    private fun respond(
        code: Int,
        body: String,
    ) = server.enqueue(
        MockResponse
            .Builder()
            .code(code)
            .body(body)
            .build(),
    )

    @Test
    fun successReturnsTheBody() =
        runTest {
            respond(202, """{"status":"accepted"}""")
            val result = apiCall { api.resendCode(EmailRequest("ana@example.cl")) }
            assertEquals(ApiResult.Success(AcceptedDto("accepted")), result)
        }

    @Test
    fun problemJsonBecomesItsCode() =
        runTest {
            respond(401, """{"status":401,"code":"AUTH_INVALID_CREDENTIALS"}""")
            val result = apiCall { api.resendCode(EmailRequest("ana@example.cl")) }
            assertEquals(ApiResult.Problem("AUTH_INVALID_CREDENTIALS"), result)
        }

    @Test
    fun unreadableErrorIsUnexpected() =
        runTest {
            respond(502, "<html>bad gateway</html>")
            assertEquals(ApiResult.Unexpected, apiCall { api.resendCode(EmailRequest("ana@example.cl")) })
        }

    @Test
    fun connectionFailureIsOffline() =
        runTest {
            server.close()
            assertEquals(ApiResult.Offline, apiCall { api.resendCode(EmailRequest("ana@example.cl")) })
        }
}
