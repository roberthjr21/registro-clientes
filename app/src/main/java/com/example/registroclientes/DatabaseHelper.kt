package com.example.registroclientes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Cliente(val id: Long, val nombre: String, val correo: String, val telefono: String)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "ClientesDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE clientes (
                id INTEGER PRIMARY KEY,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL,
                telefono TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        error("Falta la migración de la base de datos: $oldVersion a $newVersion")
    }

    fun insertarCliente(cliente: Cliente): Boolean {
        val datos = valores(cliente).apply { put("id", cliente.id) }
        return writableDatabase.insertOrThrow("clientes", null, datos) != -1L
    }

    fun consultarCliente(id: Long): Cliente? =
        readableDatabase.query(
            "clientes", null, "id = ?", arrayOf(id.toString()), null, null, null
        ).use { cursor ->
            if (cursor.moveToFirst()) leerCliente(cursor) else null
        }

    fun consultarTodos(): List<Cliente> =
        readableDatabase.query(
            "clientes", null, null, null, null, null, "id ASC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(leerCliente(cursor))
            }
        }

    fun actualizarCliente(cliente: Cliente): Boolean =
        writableDatabase.update(
            "clientes", valores(cliente), "id = ?", arrayOf(cliente.id.toString())
        ) > 0

    fun eliminarCliente(id: Long): Boolean =
        writableDatabase.delete("clientes", "id = ?", arrayOf(id.toString())) > 0

    private fun valores(cliente: Cliente) = ContentValues().apply {
        put("nombre", cliente.nombre)
        put("correo", cliente.correo)
        put("telefono", cliente.telefono)
    }

    private fun leerCliente(cursor: Cursor) = Cliente(
        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
        correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
    )
}
