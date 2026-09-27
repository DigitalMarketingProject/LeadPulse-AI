package com.marketing.leadscore.controller;

import com.marketing.leadscore.service.ExcelExportService;
import com.marketing.leadscore.service.PDFExportService;

import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExportController {

    private final ExcelExportService excelService;

       public ExportController(
        ExcelExportService excelService,
        PDFExportService pdfService){

    this.excelService=excelService;
    this.pdfService=pdfService;

}

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() throws Exception {

        byte[] excel = excelService.exportCustomers();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_OCTET_STREAM);

        headers.set(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=LeadPulse_Report.xlsx");

        return new ResponseEntity<>(
                excel,
                headers,
                HttpStatus.OK);

    }

    private final PDFExportService pdfService;

    @GetMapping("/export/pdf")
public ResponseEntity<byte[]> exportPDF()
        throws Exception {

    byte[] pdf =
            pdfService.exportPDF();

    HttpHeaders headers =
            new HttpHeaders();

    headers.setContentType(
            MediaType.APPLICATION_PDF);

    headers.set(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=LeadPulse_Report.pdf");

    return new ResponseEntity<>(
            pdf,
            headers,
            HttpStatus.OK);

}

}