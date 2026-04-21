package com.gymapp.gymmanager.controller

import com.gymapp.gymmanager.service.ExcelService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/reports")
class ReportController(private val excelService: ExcelService) {

    @GetMapping("/members")
    fun downloadMembersReport(): ResponseEntity<ByteArray> {
        val excel = excelService.generateMembersReport()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=miembros.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel)
    }

    @GetMapping("/template")
    fun downloadTemplate(): ResponseEntity<ByteArray> {
        val excel = excelService.generateTemplate()
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_miembros.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(excel)
    }

    @PostMapping("/import", consumes = ["multipart/form-data"])
    fun importMembers(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, Any>> {
        val result = excelService.importMembers(file)
        return ResponseEntity.ok(result)
    }
}