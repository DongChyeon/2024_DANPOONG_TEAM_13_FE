package com.daon.onjung.network.adapter

sealed class ApiResult<T> {

    class Success<T>(val data: T?) : ApiResult<T>()

    class ApiError<T>(val message: String, val code: String) : ApiResult<T>()

    class NetworkError<T>(val throwable: Throwable) : ApiResult<T>()

}