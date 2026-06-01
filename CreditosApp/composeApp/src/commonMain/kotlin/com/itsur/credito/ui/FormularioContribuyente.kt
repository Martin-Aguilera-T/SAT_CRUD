package com.itsur.credito.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContribuyente(viewModel: ContribuyenteViewModel) {
    // Escuchar los estados del ViewModel de forma reactiva
    val rfc by viewModel.rfc.collectAsState()
    val tipoPersona by viewModel.tipoPersona.collectAsState()
    val estados by viewModel.estados.collectAsState(initial = emptyList())
    val municipios by viewModel.municipios.collectAsState()
    val estadoSeleccionado by viewModel.estadoSeleccionado.collectAsState()
    val municipioSeleccionado by viewModel.municipioSeleccionado.collectAsState()

    // Controladores para expandir los ComboBox
    var expTipoPersona by remember { mutableStateOf(false) }
    var expEstado by remember { mutableStateOf(false) }
    var expMunicipio by remember { mutableStateOf(false) }

    val opcionesTipoPersona = listOf("FÍSICA", "MORAL")

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        // 1. Campo de texto para el RFC
        OutlinedTextField(
            value = rfc,
            onValueChange = { if (it.length <= 13) viewModel.rfc.value = it.uppercase() },
            label = { Text("RFC") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. ComboBox para el Tipo de Persona (Física o Moral)
        ExposedDropdownMenuBox(
            expanded = expTipoPersona,
            onExpandedChange = { expTipoPersona = !expTipoPersona }
        ) {
            OutlinedTextField(
                value = tipoPersona,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de Persona") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expTipoPersona) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expTipoPersona,
                onDismissRequest = { expTipoPersona = false }
            ) {
                opcionesTipoPersona.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            viewModel.tipoPersona.value = opcion
                            expTipoPersona = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 3. ComboBox para los Estados
        ExposedDropdownMenuBox(
            expanded = expEstado,
            onExpandedChange = { expEstado = !expEstado }
        ) {
            OutlinedTextField(
                value = estadoSeleccionado?.nombre ?: "Selecciona Estado",
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expEstado) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expEstado,
                onDismissRequest = { expEstado = false }
            ) {
                estados.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado.nombre) },
                        onClick = {
                            viewModel.seleccionarEstado(estado)
                            expEstado = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. ComboBox para los Municipios (Filtrado Dinámico)
        ExposedDropdownMenuBox(
            expanded = expMunicipio,
            onExpandedChange = { if (estadoSeleccionado != null) expMunicipio = !expMunicipio }
        ) {
            OutlinedTextField(
                value = municipioSeleccionado?.nombre ?: "Selecciona Municipio",
                onValueChange = {},
                readOnly = true,
                label = { Text("Municipio") },
                enabled = estadoSeleccionado != null,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expMunicipio) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expMunicipio,
                onDismissRequest = { expMunicipio = false }
            ) {
                municipios.forEach { municipio ->
                    DropdownMenuItem(
                        text = { Text(municipio.nombre) },
                        onClick = {
                            viewModel.municipioSeleccionado.value = municipio
                            expMunicipio = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Guardar (Se habilita solo si el formulario está completo)
        Button(
            onClick = { viewModel.guardarContribuyente() },
            enabled = rfc.isNotBlank() && estadoSeleccionado != null && municipioSeleccionado != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}