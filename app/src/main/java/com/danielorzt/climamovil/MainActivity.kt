package com.danielorzt.climamovil

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielorzt.climamovil.data.ClimaException
import com.danielorzt.climamovil.data.DbHelper
import com.danielorzt.climamovil.data.Ubicacion
import com.danielorzt.climamovil.data.WeatherRepository
import com.danielorzt.climamovil.databinding.ActivityMainBinding
import com.danielorzt.climamovil.databinding.DialogAgregarBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repo: WeatherRepository
    private val adapter = UbicacionAdapter { ubicacion -> eliminar(ubicacion) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        repo = WeatherRepository(DbHelper(applicationContext))

        binding.lista.layoutManager = LinearLayoutManager(this)
        binding.lista.adapter = adapter
        binding.fabAgregar.setOnClickListener { mostrarDialogoAgregar() }

        // La lista se lee siempre de SQLite, por eso funciona sin internet.
        lifecycleScope.launch { refrescarLista() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.accion_actualizar) {
            actualizarTodas()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private suspend fun refrescarLista() {
        val datos = repo.listar()
        adapter.submitList(datos)
        binding.tvVacio.isVisible = datos.isEmpty()
    }

    private fun mostrarDialogoAgregar() {
        val dialogo = DialogAgregarBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.agregar_titulo)
            .setView(dialogo.root)
            .setPositiveButton(R.string.agregar) { _, _ ->
                agregar(dialogo.campoCiudad.text?.toString().orEmpty())
            }
            .setNegativeButton(R.string.cancelar, null)
            .show()
    }

    private fun agregar(ciudad: String) {
        if (ciudad.isBlank()) {
            mensaje(getString(R.string.escribe_ciudad))
            return
        }
        lifecycleScope.launch {
            cargando(true)
            try {
                repo.agregar(ciudad)
                refrescarLista()
            } catch (e: ClimaException) {
                mensaje(e.message.orEmpty())
            } finally {
                cargando(false)
            }
        }
    }

    private fun eliminar(ubicacion: Ubicacion) {
        lifecycleScope.launch {
            repo.eliminar(ubicacion.id)
            refrescarLista()
            mensaje(getString(R.string.eliminada, ubicacion.ciudad))
        }
    }

    private fun actualizarTodas() {
        lifecycleScope.launch {
            cargando(true)
            val (ok, fallos) = repo.actualizarTodas()
            refrescarLista()
            cargando(false)
            mensaje(
                when {
                    ok == 0 && fallos == 0 -> getString(R.string.nada_que_actualizar)
                    fallos == 0 -> getString(R.string.actualizadas, ok)
                    else -> getString(R.string.actualizadas_con_fallos, ok, fallos)
                }
            )
        }
    }

    private fun cargando(visible: Boolean) {
        binding.progreso.isVisible = visible
    }

    private fun mensaje(texto: String) {
        Snackbar.make(binding.root, texto, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.fabAgregar)
            .show()
    }
}
