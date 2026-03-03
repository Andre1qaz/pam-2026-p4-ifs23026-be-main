package org.delcom.services

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import org.delcom.data.AppException
import org.delcom.data.DataResponse
import org.delcom.data.ShoeRequest
import org.delcom.helpers.ValidatorHelper
import org.delcom.repositories.IShoeRepository
import java.io.File
import java.util.*

class ShoeService(private val shoeRepository: IShoeRepository) {

    // GET /shoes?search=
    suspend fun getAllShoes(call: ApplicationCall) {
        val search = call.request.queryParameters["search"] ?: ""
        val shoes = shoeRepository.getShoes(search)
        call.respond(DataResponse("success", "Berhasil mengambil daftar sepatu", mapOf("shoes" to shoes)))
    }

    // GET /shoes/{id}
    suspend fun getShoeById(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID sepatu tidak boleh kosong!")
        val shoe = shoeRepository.getShoeById(id) ?: throw AppException(404, "Data sepatu tidak tersedia!")
        call.respond(DataResponse("success", "Berhasil mengambil data sepatu", mapOf("shoe" to shoe)))
    }

    private suspend fun getShoeRequest(call: ApplicationCall): ShoeRequest {
        val req = ShoeRequest()
        val multipart = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 5)
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> when (part.name) {
                    "nama"      -> req.nama      = part.value.trim()
                    "merek"     -> req.merek     = part.value.trim()
                    "tipe"      -> req.tipe      = part.value.trim()
                    "ukuran"    -> req.ukuran    = part.value.trim()
                    "deskripsi" -> req.deskripsi = part.value
                }
                is PartData.FileItem -> {
                    val ext = part.originalFileName?.substringAfterLast(".", "")?.let { if (it.isNotEmpty()) ".$it" else "" } ?: ""
                    val fileName = UUID.randomUUID().toString() + ext
                    val filePath = "uploads/shoes/$fileName"
                    val file = File(filePath)
                    file.parentFile.mkdirs()
                    part.provider().copyAndClose(file.writeChannel())
                    req.pathGambar = filePath
                }
                else -> {}
            }
            part.dispose()
        }
        return req
    }

x    private fun validateShoeRequest(req: ShoeRequest) {
        val v = ValidatorHelper(req.toMap())
        v.required("nama", "Nama sepatu tidak boleh kosong")
        v.required("merek", "Merek tidak boleh kosong")
        v.required("tipe", "Tipe tidak boleh kosong")
        v.required("ukuran", "Ukuran tidak boleh kosong")
        v.required("deskripsi", "Deskripsi tidak boleh kosong")
        v.required("pathGambar", "Gambar tidak boleh kosong")
        v.validate()
        if (!File(req.pathGambar).exists()) throw AppException(400, "Gambar sepatu gagal diupload!")
    }

    suspend fun createShoe(call: ApplicationCall) {
        val req = getShoeRequest(call)
        validateShoeRequest(req)
        val existing = shoeRepository.getShoeByName(req.nama)
        if (existing != null) {
            File(req.pathGambar).takeIf { it.exists() }?.delete()
            throw AppException(409, "Sepatu dengan nama ini sudah terdaftar!")
        }
        val shoeId = shoeRepository.addShoe(req.toEntity())
        call.respond(DataResponse("success", "Berhasil menambahkan data sepatu", mapOf("shoeId" to shoeId)))
    }

    suspend fun updateShoe(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID sepatu tidak boleh kosong!")
        val oldShoe = shoeRepository.getShoeById(id) ?: throw AppException(404, "Data sepatu tidak tersedia!")
        val req = getShoeRequest(call)
        if (req.pathGambar.isEmpty()) req.pathGambar = oldShoe.pathGambar
        validateShoeRequest(req)
        if (req.nama != oldShoe.nama) {
            val existing = shoeRepository.getShoeByName(req.nama)
            if (existing != null) {
                File(req.pathGambar).takeIf { it.exists() }?.delete()
                throw AppException(409, "Sepatu dengan nama ini sudah terdaftar!")
            }
        }
        if (req.pathGambar != oldShoe.pathGambar) File(oldShoe.pathGambar).takeIf { it.exists() }?.delete()
        val updated = shoeRepository.updateShoe(id, req.toEntity())
        if (!updated) throw AppException(400, "Gagal memperbarui data sepatu!")
        call.respond(DataResponse("success", "Berhasil mengubah data sepatu", null))
    }

    suspend fun deleteShoe(call: ApplicationCall) {
        val id = call.parameters["id"] ?: throw AppException(400, "ID sepatu tidak boleh kosong!")
        val oldShoe = shoeRepository.getShoeById(id) ?: throw AppException(404, "Data sepatu tidak tersedia!")
        val oldFile = File(oldShoe.pathGambar)
        val deleted = shoeRepository.removeShoe(id)
        if (!deleted) throw AppException(400, "Gagal menghapus data sepatu!")
        oldFile.takeIf { it.exists() }?.delete()
        call.respond(DataResponse("success", "Berhasil menghapus data sepatu", null))
    }

    suspend fun getShoeImage(call: ApplicationCall) {
        val id = call.parameters["id"] ?: return call.respond(HttpStatusCode.BadRequest)
        val shoe = shoeRepository.getShoeById(id) ?: return call.respond(HttpStatusCode.NotFound)
        val file = File(shoe.pathGambar)
        if (!file.exists()) return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}
