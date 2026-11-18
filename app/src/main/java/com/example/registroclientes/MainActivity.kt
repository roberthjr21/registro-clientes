package com.example.registroclientes

import android.app.Activity
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var etId: EditText
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etTelefono: EditText
    private lateinit var tvResultado: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbHelper = DatabaseHelper(this)

        etId = findViewById(R.id.etId)
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etTelefono = findViewById(R.id.etTelefono)
        tvResultado = findViewById(R.id.tvResultado)

        findViewById<Button>(R.id.btnGuardar).setOnClickListener { ejecutar { guardar() } }
        findViewById<Button>(R.id.btnConsultar).setOnClickListener { ejecutar { consultar() } }
        findViewById<Button>(R.id.btnActualizar).setOnClickListener { ejecutar { actualizar() } }
        findViewById<Button>(R.id.btnEliminar).setOnClickListener { ejecutar { eliminar() } }
        findViewById<Button>(R.id.btnLimpiar).setOnClickListener {
            limpiarFormulario()
            tvResultado.setText(R.string.result_placeholder)
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    private fun guardar() {
        val cliente = leerFormulario() ?: return
        try {
            if (dbHelper.insertarCliente(cliente)) {
                limpiarFormulario()
                tvResultado.setText(R.string.saved)
            } else {
                tvResultado.setText(R.string.database_error)
            }
        } catch (_: SQLiteConstraintException) {
            etId.error = getString(R.string.duplicate)
            etId.requestFocus()
            tvResultado.setText(R.string.duplicate)
        }
    }

    private fun consultar() {
        if (etId.text.isBlank()) {
            val clientes = dbHelper.consultarTodos()
            tvResultado.text = if (clientes.isEmpty()) {
                getString(R.string.no_clients)
            } else {
                clientes.joinToString("\n\n", transform = ::formatear)
            }
            return
        }

        val id = leerId() ?: return
        val cliente = dbHelper.consultarCliente(id)
        if (cliente == null) {
            tvResultado.setText(R.string.not_found)
            return
        }

        etNombre.setText(cliente.nombre)
        etCorreo.setText(cliente.correo)
        etTelefono.setText(cliente.telefono)
        tvResultado.text = formatear(cliente)
    }

    private fun actualizar() {
        val cliente = leerFormulario() ?: return
        val actualizado = dbHelper.actualizarCliente(cliente)
        tvResultado.setText(if (actualizado) R.string.updated else R.string.not_found)
    }

    private fun eliminar() {
        val id = leerId() ?: return
        val eliminado = dbHelper.eliminarCliente(id)
        if (eliminado) limpiarFormulario()
        tvResultado.setText(if (eliminado) R.string.deleted else R.string.not_found)
    }

    private fun leerId(): Long? {
        val id = etId.text.toString().trim().toLongOrNull()
        if (id == null || id <= 0) {
            etId.error = getString(R.string.id_error)
            etId.requestFocus()
            return null
        }
        return id
    }

    private fun leerFormulario(): Cliente? {
        val id = leerId() ?: return null
        val campoVacio = listOf(etNombre, etCorreo, etTelefono).firstOrNull { it.text.isBlank() }
        if (campoVacio != null) {
            campoVacio.error = getString(R.string.required)
            campoVacio.requestFocus()
            return null
        }

        val correo = etCorreo.text.toString().trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.error = getString(R.string.email_error)
            etCorreo.requestFocus()
            return null
        }

        return Cliente(id, etNombre.text.toString().trim(), correo, etTelefono.text.toString().trim())
    }

    private fun limpiarFormulario() {
        for (campo in listOf(etId, etNombre, etCorreo, etTelefono)) {
            campo.text.clear()
            campo.error = null
        }
        etId.requestFocus()
    }

    private fun formatear(cliente: Cliente): String =
        getString(R.string.record, cliente.id, cliente.nombre, cliente.correo, cliente.telefono)

    private fun ejecutar(operacion: () -> Unit) {
        try {
            operacion()
        } catch (e: SQLiteException) {
            Log.e("RegistroClientes", "Error de SQLite", e)
            tvResultado.setText(R.string.database_error)
        }
    }
}
