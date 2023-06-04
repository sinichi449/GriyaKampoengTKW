package net.bagusekasaputra.griyakampoengtkw.presentation.model

sealed class UiState<T> {

    data class Loading<T>(val content: T? = null): UiState<T>()

    data class Success<T>(val data: T? = null): UiState<T>()

    data class Failure<T>(val failMsg: String?): UiState<T>()

}