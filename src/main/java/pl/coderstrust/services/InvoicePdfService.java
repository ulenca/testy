package pl.coderstrust.services;

import org.springframework.stereotype.Service;

@Service
public class InvoicePdfService {
    /*
    Add support for PDF generation: user should be able to download saved invoice in PDF format.

        TIPs:
        - create `InvoicePdfService` service
       - add/update endpoint in REST controller that allows download invoice as PDF
       - use `itextpdf` dependency to create PDF
       - do not save created PDF to disc - keep PDF in memory (`ByteArrayOutputStream`)

        `public byte[] createPdf(Invocie invoice)`
     */







}
