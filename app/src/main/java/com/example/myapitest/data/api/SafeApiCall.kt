package com.example.myapitest.data.api

import retrofit2.HttpException

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val code: Int, val message: String) : Result<Nothing>()

    companion object {
        const val UNKNOWN_ERROR_CODE = -1
        const val UNKNOWN_ERROR_MESSAGE = "An unknown error occurred"
    }
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
    return try {
        Result.Success(apiCall())
    } catch (exception: Exception) {
        when (exception) {
            is HttpException -> {
                Result.Error(exception.code(), exception.message ?: "HTTP error")
            }
            else -> {
                Result.Error(Result.UNKNOWN_ERROR_CODE, Result.UNKNOWN_ERROR_MESSAGE)
            }
        }
    }
}
