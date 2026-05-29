package com.itsur.credito

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.itsur.credito.ui.FormularioContribuyente
import com.itsur.credito.ui.ContribuyenteViewModel
import com.itsur.credito.domain.ContribuyenteRepository

@Composable
fun App(repository: ContribuyenteRepository) {
    MaterialTheme {
        // Usamos remember para que el ViewModel se cree UNA sola vez y no se destruya al recomponer
        val viewModel = remember { ContribuyenteViewModel(repository) }

        // Renderizamos tu formulario reactivo pasando la instancia estable
        FormularioContribuyente(viewModel = viewModel)
    }
}