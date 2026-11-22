package com.abitusd.app.core.network

import android.util.Log
import com.abitusd.app.BuildConfig
import com.abitusd.app.core.network.dto.ProblemDto
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

/** Outcome of a network call. The UI maps problem codes to localized texts. */
sealed interface ApiResult<out T> {
    data class Success<T>(
        val value: T,
    ) : ApiResult<T>

    data class Problem(
        val code: String,
    ) : ApiResult<Nothing>

    data object Offline : ApiResult<Nothing>

    data object Unexpected : ApiResult<Nothing>
}

private val problemJson = Json { ignoreUnknownKeys = true }

/** Runs a call and turns HTTP and connectivity failures into [ApiResult] values. */
@Suppress("TooGenericExceptionCaught")
suspend fun <T> apiCall(block: suspend () -> T): ApiResult<T> =
    try {
        ApiResult.Success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: HttpException) {
        val body = error.response()?.errorBody()?.string()
        val code = body?.let { runCatching { problemJson.decodeFromString<ProblemDto>(it).code }.getOrNull() }
        if (code != null) ApiResult.Problem(code) else ApiResult.Unexpected
    } catch (error: IOException) {
        debugLog(error)
        ApiResult.Offline
    } catch (error: Exception) {
        debugLog(error)
        ApiResult.Unexpected
    }

/** Only the exception type in debug builds: messages may contain hosts or user data. */
private fun debugLog(error: Exception) {
    if (BuildConfig.DEBUG) Log.w("ApiResult", "request failed: ${error.javaClass.name}")
}
