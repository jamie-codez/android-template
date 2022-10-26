package io.bloco.template.features.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.bloco.core.domain.GetBook
import io.bloco.core.domain.models.BookDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailsViewModel @AssistedInject constructor(
    @Assisted bookId: String,
    getBook: GetBook,
) : ViewModel() {

    private val _bookDetailsUpdateState =
        MutableStateFlow<DetailsScreenUiState>(DetailsScreenUiState.LoadingFromAPI)
    val bookDetailsUpdateState = _bookDetailsUpdateState.asStateFlow()

    init {
        viewModelScope.launch {
            getBook(bookId)
                .onSuccess { _bookDetailsUpdateState.value = DetailsScreenUiState.Success(it) }
                .onFailure { _bookDetailsUpdateState.value = DetailsScreenUiState.ErrorFromAPI }
        }
    }

    sealed interface DetailsScreenUiState {
        object LoadingFromAPI : DetailsScreenUiState
        data class Success(val book: BookDetails) : DetailsScreenUiState
        object ErrorFromAPI : DetailsScreenUiState
    }

    @AssistedFactory
    interface Factory {
        fun create(bookId: String): DetailsViewModel
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun provideFactory(
            factory: Factory,
            bookId: String,
        ) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return factory.create(bookId) as T
            }
        }
    }
}
