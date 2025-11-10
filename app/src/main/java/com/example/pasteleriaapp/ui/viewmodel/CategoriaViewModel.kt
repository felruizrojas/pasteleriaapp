package com.example.pasteleriaapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pasteleriaapp.domain.model.Categoria
import com.example.pasteleriaapp.domain.repository.CategoriaRepository
import com.example.pasteleriaapp.ui.state.CategoriaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriaViewModel(
    private val repository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaUiState())
    val uiState: StateFlow<CategoriaUiState> = _uiState.asStateFlow()

    // --- NUEVO: Lista interna para guardar todas las categorías ---
    private var listaCompletaCategorias: List<Categoria> = emptyList()

    init {
        cargarCategorias()
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(estaCargando = true) }

            repository.obtenerCategorias()
                .catch { e ->
                    _uiState.update { it.copy(estaCargando = false, error = e.message) }
                }
                .collect { categorias ->
                    // Guardamos la lista completa internamente
                    listaCompletaCategorias = categorias

                    // Actualizamos la UI (inicialmente sin filtro)
                    _uiState.update {
                        it.copy(
                            estaCargando = false,
                            categorias = categorias, // La lista filtrada es igual a la completa
                            error = null
                        )
                    }
                }
        }
    }

    // --- FUNCIÓN NUEVA: Se llama cada vez que el usuario escribe ---
    fun onSearchQueryChange(query: String) {
        // 1. Actualiza el texto en la UI
        _uiState.update { it.copy(searchQuery = query) }

        // 2. Filtra la lista
        val listaFiltrada = if (query.isBlank()) {
            listaCompletaCategorias // Si no hay búsqueda, muestra todo
        } else {
            // Filtra por nombre de categoría
            listaCompletaCategorias.filter { categoria ->
                categoria.nombreCategoria.contains(query, ignoreCase = true)
            }
        }

        // 3. Actualiza la lista de categorías que ve el usuario
        _uiState.update { it.copy(categorias = listaFiltrada) }
    }
}

// ... (CategoriaViewModelFactory no necesita cambios) ...
class CategoriaViewModelFactory(
    private val repository: CategoriaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            return CategoriaViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}