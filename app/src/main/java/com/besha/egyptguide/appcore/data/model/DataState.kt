package com.besha.egyptguide.appcore.data.model

sealed class DataState<out T>(
    val loading: Boolean,
    private val data: T? = null,
    private val code: Int? = null

) {
    fun data(): T? = data

    object Idle : DataState<Nothing>(false)

    class Loading<T>(val cachedData: T? = null) : DataState<T>(
        loading = true,
        data = cachedData
    )

    class Error<T>(val throwable: Throwable, val code: Int = 400) : DataState<T>(
        loading = false,
        code = code,
        data = null
    )

    data class Success<out T>(val data: T) : DataState<T>(
        loading = false,
        data = data
    )

    object Empty : DataState<Nothing>(false)
}

fun <T> DataState<List<T>>.isEmptyList(): Boolean {
    return this is DataState.Success && this.data.isNullOrEmpty()
}
