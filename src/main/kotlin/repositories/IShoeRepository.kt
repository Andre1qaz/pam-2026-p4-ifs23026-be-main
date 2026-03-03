package org.delcom.repositories

import org.delcom.entities.Shoe

interface IShoeRepository {
    suspend fun getShoes(search: String): List<Shoe>
    suspend fun getShoeById(id: String): Shoe?
    suspend fun getShoeByName(name: String): Shoe?
    suspend fun addShoe(shoe: Shoe): String
    suspend fun updateShoe(id: String, newShoe: Shoe): Boolean
    suspend fun removeShoe(id: String): Boolean
}
