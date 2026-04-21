package com.gymapp.gymmanager.service

import com.gymapp.gymmanager.repository.MemberRepository
import com.gymapp.gymmanager.repository.MembershipRepository
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate

@Service
class ExcelService(
    private val memberRepository: MemberRepository,
    private val membershipRepository: MembershipRepository
) {

    fun generateMembersReport(): ByteArray {
        val workbook = XSSFWorkbook()

        // ── Hoja 1: Resumen ──────────────────────────────────────────
        val summary = workbook.createSheet("Resumen")
        val today = LocalDate.now()

        val titleStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont()
            font.bold = true
            font.fontHeightInPoints = 14
            setFont(font)
        }
        val labelStyle = workbook.createCellStyle().apply {
            val font = workbook.createFont()
            font.bold = true
            setFont(font)
        }

        summary.createRow(0).createCell(0).apply {
            setCellValue("Reporte Gym Manager — ${today}")
            cellStyle = titleStyle
        }
        summary.addMergedRegion(CellRangeAddress(0, 0, 0, 2))

        val members = memberRepository.findAll()
        var alDia = 0; var porVencer = 0; var vencidos = 0; var inactivos = 0; var sinMembresia = 0

        members.forEach { m ->
            if (!m.activo) { inactivos++; return@forEach }
            val last = membershipRepository.findByMemberId(m.id!!).maxByOrNull { it.fechaVencimiento }
            if (last == null) { sinMembresia++; return@forEach }
            val venc = last.fechaVencimiento
            when {
                venc.isBefore(today) -> vencidos++
                venc.isBefore(today.plusDays(5)) -> porVencer++
                else -> alDia++
            }
        }

        listOf(
            Pair("Total miembros", members.size),
            Pair("Al día", alDia),
            Pair("Por vencer (próx. 5 días)", porVencer),
            Pair("Vencidos", vencidos),
            Pair("Sin membresía", sinMembresia),
            Pair("Inactivos", inactivos)
        ).forEachIndexed { i, (label, value) ->
            val row = summary.createRow(i + 2)
            row.createCell(0).apply { setCellValue(label); cellStyle = labelStyle }
            row.createCell(1).setCellValue(value.toDouble())
        }

        summary.setColumnWidth(0, 8000)
        summary.setColumnWidth(1, 4000)

        // ── Hoja 2: Miembros ─────────────────────────────────────────
        val sheet = workbook.createSheet("Miembros")

        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.DARK_BLUE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = workbook.createFont()
            font.bold = true
            font.color = IndexedColors.WHITE.index
            setFont(font)
        }
        val redStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.ROSE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }
        val yellowStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.LIGHT_YELLOW.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }
        val greenStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.LIGHT_GREEN.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }
        val grayStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
        }

        val headers = listOf("ID", "Nombre", "Teléfono", "Email", "Activo", "Último Pago", "Vencimiento", "Monto", "Método", "Estado")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { i, title ->
            headerRow.createCell(i).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        members.forEachIndexed { index, member ->
            val row = sheet.createRow(index + 1)
            val lastMembership = membershipRepository
                .findByMemberId(member.id!!)
                .maxByOrNull { it.fechaVencimiento }

            val vencimiento = lastMembership?.fechaVencimiento
            val estado = when {
                !member.activo -> "Inactivo"
                vencimiento == null -> "Sin membresía"
                vencimiento.isBefore(today) -> "Vencido"
                vencimiento.isBefore(today.plusDays(5)) -> "Por vencer"
                else -> "Al día"
            }

            val rowStyle = when (estado) {
                "Vencido" -> redStyle
                "Por vencer" -> yellowStyle
                "Al día" -> greenStyle
                "Inactivo" -> grayStyle
                else -> null
            }

            row.createCell(0).setCellValue(member.id!!.toDouble())
            row.createCell(1).setCellValue(member.nombre)
            row.createCell(2).setCellValue(member.telefono)
            row.createCell(3).setCellValue(member.email)
            row.createCell(4).setCellValue(if (member.activo) "Sí" else "No")
            row.createCell(5).setCellValue(lastMembership?.fechaPago?.toString() ?: "N/A")
            row.createCell(6).setCellValue(vencimiento?.toString() ?: "N/A")
            row.createCell(7).setCellValue(lastMembership?.montoPagado?.toDouble() ?: 0.0)
            row.createCell(8).setCellValue(lastMembership?.metodoPago ?: "N/A")
            row.createCell(9).setCellValue(estado)

            if (rowStyle != null) {
                (0..9).forEach { row.getCell(it)?.cellStyle = rowStyle }
            }
        }

        (0..9).forEach { sheet.autoSizeColumn(it) }

        // ── Hoja 3: Historial completo ───────────────────────────────
        val historial = workbook.createSheet("Historial pagos")

        val hHeaders = listOf("Miembro", "Teléfono", "Fecha Pago", "Vencimiento", "Monto", "Método")
        val hHeaderRow = historial.createRow(0)
        hHeaders.forEachIndexed { i, title ->
            hHeaderRow.createCell(i).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        var rowIndex = 1
        members.forEach { member ->
            val pagos = membershipRepository.findByMemberId(member.id!!)
                .sortedByDescending { it.fechaPago }
            pagos.forEach { pago ->
                val row = historial.createRow(rowIndex++)
                row.createCell(0).setCellValue(member.nombre)
                row.createCell(1).setCellValue(member.telefono)
                row.createCell(2).setCellValue(pago.fechaPago.toString())
                row.createCell(3).setCellValue(pago.fechaVencimiento.toString())
                row.createCell(4).setCellValue(pago.montoPagado.toDouble())
                row.createCell(5).setCellValue(pago.metodoPago)
            }
        }

        (0..5).forEach { historial.autoSizeColumn(it) }

        val out = ByteArrayOutputStream()
        workbook.write(out)
        workbook.close()
        return out.toByteArray()
    }

    fun generateTemplate(): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Miembros")

        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.DARK_BLUE.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            val font = workbook.createFont()
            font.bold = true
            font.color = IndexedColors.WHITE.index
            setFont(font)
        }

        val headers = listOf("nombre", "telefono", "email", "fechaPago", "fechaVencimiento", "montoPagado", "metodoPago")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { i, title ->
            headerRow.createCell(i).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        // Fila de ejemplo
        val example = sheet.createRow(1)
        example.createCell(0).setCellValue("Juan Pérez")
        example.createCell(1).setCellValue("9611234567")
        example.createCell(2).setCellValue("juan@gmail.com")
        example.createCell(3).setCellValue("2026-04-20")
        example.createCell(4).setCellValue("2026-05-20")
        example.createCell(5).setCellValue(500.0)
        example.createCell(6).setCellValue("Efectivo")

        (0..6).forEach { sheet.autoSizeColumn(it) }

        val out = ByteArrayOutputStream()
        workbook.write(out)
        workbook.close()
        return out.toByteArray()
    }

    fun importMembers(file: org.springframework.web.multipart.MultipartFile): Map<String, Any> {
        val workbook = XSSFWorkbook(file.inputStream)
        val sheet = workbook.getSheetAt(0)
        var imported = 0
        var errors = 0
        val errorList = mutableListOf<String>()

        fun getCellAsString(cell: org.apache.poi.ss.usermodel.Cell?): String {
            if (cell == null) return ""
            return when (cell.cellType) {
                org.apache.poi.ss.usermodel.CellType.STRING -> cell.stringCellValue
                org.apache.poi.ss.usermodel.CellType.NUMERIC -> {
                    if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                        cell.localDateTimeCellValue.toLocalDate().toString()
                    } else {
                        cell.numericCellValue.toLong().toString()
                    }
                }
                else -> ""
            }
        }

        for (i in 1..sheet.lastRowNum) {
            val row = sheet.getRow(i) ?: continue
            try {
                val nombre = getCellAsString(row.getCell(0))
                if (nombre.isBlank()) continue
                val telefono = getCellAsString(row.getCell(1))
                val email = getCellAsString(row.getCell(2))
                val fechaPago = LocalDate.parse(getCellAsString(row.getCell(3)))
                val fechaVencimiento = LocalDate.parse(getCellAsString(row.getCell(4)))
                val montoPagado = row.getCell(5)?.numericCellValue?.toBigDecimal() ?: java.math.BigDecimal.ZERO
                val metodoPago = getCellAsString(row.getCell(6)).ifBlank { "Efectivo" }

                val existingMember = memberRepository.findByTelefono(telefono)
                val member = existingMember ?: memberRepository.save(
                    com.gymapp.gymmanager.entity.Member(
                        nombre = nombre,
                        telefono = telefono,
                        email = email,
                        fechaRegistro = LocalDate.now(),
                        activo = true
                    )
                )

                val yaExiste = membershipRepository.findByMemberId(member.id!!)
                    .any { it.fechaPago == fechaPago }

                if (!yaExiste) {
                    membershipRepository.save(
                        com.gymapp.gymmanager.entity.Membership(
                            member = member,
                            fechaPago = fechaPago,
                            fechaVencimiento = fechaVencimiento,
                            montoPagado = montoPagado,
                            metodoPago = metodoPago
                        )
                    )
                    imported++
                } else {
                    errorList.add("Fila ${i + 1}: ${nombre} ya tiene un pago en esa fecha")
                    errors++
                }
            } catch (e: Exception) {
                errors++
                errorList.add("Fila ${i + 1}: ${e.message}")
            }
        }

        workbook.close()
        return mapOf("imported" to imported, "errors" to errors, "errorDetails" to errorList)
    }

}