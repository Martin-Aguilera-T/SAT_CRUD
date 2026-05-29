package com.itsur.credito.domain

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.itsur.credito.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import com.itsur.credito.db.Contribuyente
import com.itsur.credito.db.Estado
import com.itsur.credito.db.Municipio

class ContribuyenteRepository(database: AppDatabase) {
    private val queries = database.appDatabaseQueries

    // Al haber limpiado el .sq, mapToList ya no requiere parámetros forzados
    fun obtenerEstados(): Flow<List<Estado>> =
        queries.getEstados().asFlow().mapToList(Dispatchers.Default)

    fun obtenerMunicipiosPorEstado(estadoId: Long): Flow<List<Municipio>> =
        queries.getMunicipiosByEstado(estadoId).asFlow().mapToList(Dispatchers.Default)

    fun obtenerContribuyentes(): Flow<List<Contribuyente>> =
        queries.getContribuyentes().asFlow().mapToList(Dispatchers.Default)

    suspend fun insertar(
        rfc: String,
        tipoPersona: String,
        regimenFiscal: String,
        estadoId: Long,
        municipioId: Long
    ) = withContext(Dispatchers.Default) {
        queries.insertContribuyente(
            rfc = rfc,
            tipoPersona = tipoPersona,
            regimenFiscal = regimenFiscal,
            estadoId = estadoId,
            municipioId = municipioId
        )
    }

    suspend fun eliminar(id: Long) = withContext(Dispatchers.Default) {
        queries.deleteContribuyente(id = id)
    }

    suspend fun preCargarCatalogos() = withContext(Dispatchers.Default) {
        if (queries.getEstados().executeAsList().isEmpty()) {
            // Insertar Estados
            queries.insertEstado(nombre = "Guanajuato")
            queries.insertEstado(nombre = "Querétaro")

            // Insertar Municipios usando los nombres de parámetros explícitos
            queries.insertMunicipio(estadoId = 1L, nombre = "Uriangato")
            queries.insertMunicipio(estadoId = 1L, nombre = "Moroleón")
            queries.insertMunicipio(estadoId = 1L, nombre = "Yuriria")

            queries.insertMunicipio(estadoId = 2L, nombre = "Santiago de Querétaro")
            queries.insertMunicipio(estadoId = 2L, nombre = "San Juan del Río")
        }
    }
}