package com.itsur.credito

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.itsur.credito.db.AppDatabase
import com.itsur.credito.domain.ContribuyenteRepository

fun main() = application {
    // 1. Inicializar el driver embebido de SQLite para escritorio en memoria
    val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    try {
        AppDatabase.Schema.create(driver)
    } catch (e: Exception) {
        // Esquema ya existente
    }

    val database = AppDatabase(driver)
    val repository = ContribuyenteRepository(database)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Gestión de Contribuyentes SAT"
    ) {
        // Enviar el repositorio configurado
        App(repository = repository)
    }
}