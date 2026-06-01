package com.itsur.credito

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.itsur.credito.ui.FormularioContribuyente
import com.itsur.credito.ui.ListaContribuyentes
import com.itsur.credito.ui.ContribuyenteViewModel
import com.itsur.credito.domain.ContribuyenteRepository

@Composable
fun App(repository: ContribuyenteRepository) {
    MaterialTheme {
        val viewModel = remember { ContribuyenteViewModel(repository) }

        // Diseñamos una distribución de dos columnas para pantallas de Escritorio
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Columna Izquierda: El Formulario de captura
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                FormularioContribuyente(viewModel = viewModel)
            }

            // Una línea divisoria elegante en medio de la pantalla
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

            // Columna Derecha: La Lista en tiempo real de registrados
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                ListaContribuyentes(viewModel = viewModel)
            }
        }
    }
}