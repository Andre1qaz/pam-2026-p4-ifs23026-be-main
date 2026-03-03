package org.delcom.repositories

import org.delcom.dao.ShoeDAO
import org.delcom.entities.Shoe
import org.delcom.helpers.daoToModelShoe
import org.delcom.helpers.suspendTransaction
import org.delcom.tables.ShoeTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.lowerCase
import java.util.UUID

class ShoeRepository : IShoeRepository {

    override suspend fun getShoes(search: String): List<Shoe> = suspendTransaction {
        if (search.isBlank()) {
            ShoeDAO.all().orderBy(ShoeTable.createdAt to SortOrder.DESC).limit(20).map(::daoToModelShoe)
        } else {
            val keyword = "%" + search.lowercase() + "%"
            ShoeDAO.find { ShoeTable.nama.lowerCase() like keyword }
                .orderBy(ShoeTable.nama to SortOrder.ASC).limit(20).map(::daoToModelShoe)
        }
    }

    override suspend fun getShoeById(id: String): Shoe? = suspendTransaction {
        ShoeDAO.find { ShoeTable.id eq UUID.fromString(id) }.limit(1).map(::daoToModelShoe).firstOrNull()
    }

    override suspend fun getShoeByName(name: String): Shoe? = suspendTransaction {
        ShoeDAO.find { ShoeTable.nama eq name }.limit(1).map(::daoToModelShoe).firstOrNull()
    }

    override suspend fun addShoe(shoe: Shoe): String = suspendTransaction {
        val dao = ShoeDAO.new {
            nama = shoe.nama; merek = shoe.merek; tipe = shoe.tipe
            ukuran = shoe.ukuran; deskripsi = shoe.deskripsi
            pathGambar = shoe.pathGambar
            createdAt = shoe.createdAt; updatedAt = shoe.updatedAt
        }
        dao.id.value.toString()
    }

    override suspend fun updateShoe(id: String, newShoe: Shoe): Boolean = suspendTransaction {
        val dao = ShoeDAO.find { ShoeTable.id eq UUID.fromString(id) }.limit(1).firstOrNull()
        if (dao != null) {
            dao.nama = newShoe.nama; dao.merek = newShoe.merek; dao.tipe = newShoe.tipe
            dao.ukuran = newShoe.ukuran; dao.deskripsi = newShoe.deskripsi
            dao.pathGambar = newShoe.pathGambar; dao.updatedAt = newShoe.updatedAt
            true
        } else false
    }

    override suspend fun removeShoe(id: String): Boolean = suspendTransaction {
        ShoeTable.deleteWhere { ShoeTable.id eq UUID.fromString(id) } == 1
    }
}
