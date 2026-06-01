package com.itsur.credito.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itsur.credito.db.Contribuyente

@Composable
fun ListaContribuyentes(viewModel: ContribuyenteViewModel) {
    // Escuchamos la lista original de la BD y el texto que escribe el usuario
    val listaByDb by viewModel.contribuyentes.collectAsState()
    val query by viewModel.textoBusqueda.collectAsState()

    // Filtramos la lista en tiempo real por el RFC (ignorando mayúsculas/minúsculas y espacios)
    val listaFiltrada = remember(listaByDb, query) {
        listaByDb.filter { contribuyente ->
            contribuyente.rfc.contains(query.trim(), ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            text = "Registrados (${listaByDb.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // BARRA DE BÚSQUEDA
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.textoBusqueda.value = it },
            label = { Text("Buscar RFC") },
            placeholder = { Text("Ej. AATR94...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Despliegue de la lista filtrada
        if (listaFiltrada.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (query.isBlank()) "No hay ningún RFC registrado aún." else "No se encontraron coincidencias.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(listaFiltrada) { contribuyente -> // <-- Ahora iteramos sobre la lista filtrada
                    TarjetaContribuyente(
                        contribuyente = contribuyente,
                        viewModel = viewModel,
                        onEliminarClick = {
                            viewModel.eliminarContribuyente(contribuyente.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TarjetaContribuyente(
    contribuyente: Contribuyente,
    viewModel: ContribuyenteViewModel,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rfcFormateado(contribuyente.rfc),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tipo: ${contribuyente.tipoPersona} | Régimen: ${contribuyente.regimenFiscal}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Ubicación: Estado ID ${contribuyente.estadoId} - Municipio ID ${contribuyente.municipioId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = { viewModel.prepararEdicion(contribuyente) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Editar", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(4.dp))

                TextButton(
                    onClick = onEliminarClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Borrar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

fun rfcFormateado(rfc: String): String {
    return rfc.uppercase().trim()
}