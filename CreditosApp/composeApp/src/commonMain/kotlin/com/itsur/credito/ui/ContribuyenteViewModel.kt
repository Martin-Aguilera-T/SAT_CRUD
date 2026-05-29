package com.itsur.credito.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itsur.credito.db.Contribuyente
import com.itsur.credito.db.Estado
import com.itsur.credito.db.Municipio
import com.itsur.credito.domain.ContribuyenteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ContribuyenteViewModel(private val repository: ContribuyenteRepository) : ViewModel() {

    // Listas provenientes de la BD
    val estados: StateFlow<List<Estado>> = repository.obtenerEstados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _municipios = MutableStateFlow<List<Municipio>>(emptyList())
    val municipios: StateFlow<List<Municipio>> = _municipios.asStateFlow()

    val contribuyentes: StateFlow<List<Contribuyente>> = repository.obtenerContribuyentes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado de los inputs del Formulario
    var rfc = MutableStateFlow("")
    var tipoPersona = MutableStateFlow("FÍSICA")
    var regimenFiscal = MutableStateFlow("")

    private val _estadoSeleccionado = MutableStateFlow<Estado?>(null)
    val estadoSeleccionado = _estadoSeleccionado.asStateFlow()

    var municipioSeleccionado = MutableStateFlow<Municipio?>(null)

    init {
        viewModelScope.launch {
            repository.preCargarCatalogos()
        }
    }

    // !! REACCIONAR AL CAMBIO DE ESTADO (FILTRADO DINÁMICO) !!
    fun seleccionarEstado(estado: Estado) {
        _estadoSeleccionado.value = estado
        municipioSeleccionado.value = null // Resetea el municipio anterior

        viewModelScope.launch {
            // Escucha y actualiza dinámicamente el catálogo de municipios
            repository.obtenerMunicipiosPorEstado(estado.id).collectLatest { lista ->
                _municipios.value = lista
            }
        }
    }

    fun guardarContribuyente() {
        val est = estadoSeleccionado.value ?: return
        val mun = municipioSeleccionado.value ?: return

        viewModelScope.launch {
            repository.insertar(
                rfc = rfc.value.uppercase().trim(),
                tipoPersona = tipoPersona.value,
                regimenFiscal = regimenFiscal.value.ifBlank { "General" },
                estadoId = est.id,
                municipioId = mun.id
            )
            limpiarCampos()
        }
    }

    private fun limpiarCampos() {
        rfc.value = ""
        tipoPersona.value = "FÍSICA"
        _estadoSeleccionado.value = null
        municipioSeleccionado.value = null
        _municipios.value = emptyList()
    }
}