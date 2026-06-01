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

    // Listas provenientes de la BD expuestas como flujos reactivos
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
    var textoBusqueda = MutableStateFlow("")

    private val _estadoSeleccionado = MutableStateFlow<Estado?>(null)
    val estadoSeleccionado = _estadoSeleccionado.asStateFlow()

    var municipioSeleccionado = MutableStateFlow<Municipio?>(null)
    val contribuyenteAEditar = MutableStateFlow<Contribuyente?>(null)

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

    // Operación inteligente: Inserta o Actualiza según sea el caso
    fun guardarContribuyente() {
        val rfcActual = rfc.value
        val tipoActual = tipoPersona.value
        val regimenActual = regimenFiscal.value.ifBlank { "General" }
        val edoId = estadoSeleccionado.value?.id ?: return
        val munId = municipioSeleccionado.value?.id ?: return
        val editando = contribuyenteAEditar.value

        viewModelScope.launch {
            if (editando != null) {
                // Modo Edición: Usa el ID del registro existente
                repository.actualizar(
                    id = editando.id,
                    rfc = rfcActual,
                    tipoPersona = tipoActual,
                    regimenFiscal = regimenActual,
                    estadoId = edoId,
                    municipioId = munId
                )
            } else {
                // Modo Creación: Inserta un nuevo registro
                repository.insertar(rfcActual, tipoActual, regimenActual, edoId, munId)
            }
            // Limpieza total del formulario al terminar la operación
            limpiarFormulario()
        }
    }

    fun eliminarContribuyente(id: Long) {
        viewModelScope.launch {
            repository.eliminar(id)
        }
    }

    // Cargar los datos del usuario seleccionado de la lista de vuelta al formulario
    fun prepararEdicion(contribuyente: Contribuyente) {
        contribuyenteAEditar.value = contribuyente
        rfc.value = contribuyente.rfc
        tipoPersona.value = contribuyente.tipoPersona
        regimenFiscal.value = contribuyente.regimenFiscal

        // Buscamos el objeto Estado correspondiente para reactivar los comboboxes
        val estado = estados.value.find { it.id == contribuyente.estadoId }
        if (estado != null) {
            seleccionarEstado(estado)
            // Esperamos un instante a que carguen los municipios antes de preseleccionar el correcto
            viewModelScope.launch {
                repository.obtenerMunicipiosPorEstado(estado.id).collectLatest { lista ->
                    _municipios.value = lista
                    val municipio = lista.find { it.id == contribuyente.municipioId }
                    municipioSeleccionado.value = municipio
                }
            }
        }
    }

    // Función Única de Limpieza (Corregida con los respaldos mutables privados)
    fun limpiarFormulario() {
        contribuyenteAEditar.value = null
        rfc.value = ""
        tipoPersona.value = "FÍSICA"
        regimenFiscal.value = ""
        _estadoSeleccionado.value = null
        municipioSeleccionado.value = null
        _municipios.value = emptyList()
        textoBusqueda.value = ""
    }
}