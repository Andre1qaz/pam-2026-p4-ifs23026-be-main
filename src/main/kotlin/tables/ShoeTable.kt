package org.delcom.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object ShoeTable : UUIDTable("shoes") {
    val nama       = varchar("nama", 150)
    val merek      = varchar("merek", 100)
    val tipe       = varchar("tipe", 100)
    val ukuran     = varchar("ukuran", 100)
    val deskripsi  = text("deskripsi")
    val pathGambar = varchar("path_gambar", 255)
    val createdAt  = timestamp("created_at")
    val updatedAt  = timestamp("updated_at")
}
