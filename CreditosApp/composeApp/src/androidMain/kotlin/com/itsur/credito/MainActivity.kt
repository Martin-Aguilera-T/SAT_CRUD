package com.itsur.credito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.itsur.credito.db.AppDatabase
import com.itsur.credito.domain.ContribuyenteRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializar el driver nativo de SQLite para Android
        val driver = AndroidSqliteDriver(AppDatabase.Schema, applicationContext, "contribuyentes.db")
        val database = AppDatabase(driver)
        val repository = ContribuyenteRepository(database)

        setContent {
            // 2. Pasar el repositorio configurado de Android
            App(repository = repository)
        }
    }
}