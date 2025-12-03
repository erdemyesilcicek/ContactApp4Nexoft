package com.erdemyesilcicek.contactapp.util

/**
 * A sealed class that represents the result of a network operation.
 * Follows the Result pattern for handling success and error states.
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Extension function to handle NetworkResult in a more functional way
 */
inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> NetworkResult.Error(message, code)
        is NetworkResult.Loading -> NetworkResult.Loading
    }
}

/**
 * Extension function to get data or null
 */
fun <T> NetworkResult<T>.getOrNull(): T? {
    return when (this) {
        is NetworkResult.Success -> data
        else -> null
    }
}

/**
 * Extension function to get data or a default value
 */
fun <T> NetworkResult<T>.getOrDefault(default: T): T {
    return when (this) {
        is NetworkResult.Success -> data
        else -> default
    }
}
