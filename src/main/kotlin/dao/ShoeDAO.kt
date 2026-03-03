package org.delcom.dao

import org.delcom.tables.ShoeTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class ShoeDAO(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, ShoeDAO>(ShoeTable)

    var nama       by ShoeTable.nama
    var merek      by ShoeTable.merek
    var tipe       by ShoeTable.tipe
    var ukuran     by ShoeTable.ukuran
    var deskripsi  by ShoeTable.deskripsi
    var pathGambar by ShoeTable.pathGambar
    var createdAt  by ShoeTable.createdAt
    var updatedAt  by ShoeTable.updatedAt
}
