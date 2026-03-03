package org.delcom.data

import kotlinx.serialization.Serializable
import org.delcom.entities.Shoe

@Serializable
data class ShoeRequest(
    var nama: String = "",
    var merek: String = "",
    var tipe: String = "",
    var ukuran: String = "",
    var deskripsi: String = "",
    var pathGambar: String = "",
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nama" to nama,
        "merek" to merek,
        "tipe" to tipe,
        "ukuran" to ukuran,
        "deskripsi" to deskripsi,
        "pathGambar" to pathGambar,
    )

    fun toEntity(): Shoe = Shoe(
        nama = nama,
        merek = merek,
        tipe = tipe,
        ukuran = ukuran,
        deskripsi = deskripsi,
        pathGambar = pathGambar,
    )
}
