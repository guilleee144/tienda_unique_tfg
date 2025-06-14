package com.example.uniqueartifacts.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.uniqueartifacts.R
import com.example.uniqueartifacts.views.Notificacion

class NotificacionesViewModel : ViewModel() {

    // 🔹 Lista reactiva de notificaciones
    private val _notificaciones = mutableStateListOf(
        Notificacion(
            icono = R.drawable.descuento,
            titulo = "NUEVO DESCUENTO DISPONIBLE",
            descripcion = "Entre en ofertas para descubrir los descuentos de la temporada."
        ),
        Notificacion(
            icono = R.drawable.caja_icono,
            titulo = "PAQUETE ENTREGADO CON ÉXITO",
            descripcion = "Su paquete ha llegado con éxito a la dirección C/Maria Sarmientos 31, ha sido entregado a las 8:31 am."
        )
    )

    // 🔹 Exposición segura de la lista
    val notificaciones: SnapshotStateList<Notificacion> = _notificaciones

    // 🔹 Agregar una notificación (puedes evitar duplicados si quieres)
    fun agregar(notificacion: Notificacion) {
        if (!_notificaciones.contains(notificacion)) {
            _notificaciones.add(notificacion)
        }
    }

    // 🔹 Eliminar una notificación específica
    fun eliminar(notificacion: Notificacion) {
        _notificaciones.remove(notificacion)
    }

    // 🔹 Eliminar todas (por si quieres un botón de "borrar todo")
    fun eliminarTodas() {
        _notificaciones.clear()
    }
}
