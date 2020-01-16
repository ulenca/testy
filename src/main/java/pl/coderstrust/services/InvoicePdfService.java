package pl.coderstrust.services;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@Service
public class InvoicePdfService {

    private static final String NEW_LINE = "\n";

    private static Logger log = LoggerFactory.getLogger(InvoicePdfService.class);

    public byte[] createPdf(Invoice invoice) throws ServiceOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null");
        }
        try {
            log.debug(String.format("Creating pdf: %s", invoice));
            Document document = new Document();
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, byteStream);

            document.open();
            document.add(getTitle(invoice));
            document.add(getCompanies(invoice));
            document.add(getTable(invoice));
            document.add(getTableEntriesDescription(invoice.getEntries()));
            document.add(getTotalValueOfInvoice(invoice.getEntries()));
            document.close();
            return byteStream.toByteArray();
        } catch (DocumentException e) {
            log.error("An error occurred during creating pdf", e);
            throw new ServiceOperationException("An error occurred during creating pdf", e);
        }
    }

    private Paragraph getTotalValueOfInvoice(List<InvoiceEntry> entries) {
        Paragraph totalValue = new Paragraph("Total ", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        totalValue.setAlignment(Element.ALIGN_RIGHT);
        double totalValueOfInvoice = 0;
        for (int i = 0; i < entries.size(); i++) {
            BigDecimal something = entries.get(i).getGrossValue();
            totalValueOfInvoice += something.doubleValue();
        }
        totalValue.add(String.valueOf(totalValueOfInvoice));
        return totalValue;
    }

    private PdfPTable getCompanies(Invoice invoice) {
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(10);
        table.setWidthPercentage(100);
        table.addCell(getCompany(invoice.getSeller(), "Seller"));
        table.addCell(getCompany(invoice.getBuyer(), "Buyer"));
        return table;
    }

    private PdfPCell getCompany(Company company, String type) {
        Paragraph companyType = new Paragraph(type, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        Paragraph name = new Paragraph(company.getName());
        Paragraph address = new Paragraph(company.getAddress());
        Paragraph taxIdentifier = new Paragraph(String.format("Tax identifier: %s", company.getTaxId()));
        Paragraph accountNumber = new Paragraph(String.format("Account no: %s", company.getAccountNumber()));
        Paragraph paragraph = new Paragraph();
        paragraph.add(companyType);
        paragraph.add(name);
        paragraph.add(address);
        paragraph.add(NEW_LINE);
        paragraph.add(accountNumber);
        paragraph.add(taxIdentifier);
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        cell.addElement(paragraph);
        return cell;
    }

    private PdfPTable getTable(Invoice invoice) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setSpacingBefore(10f);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10f);
        float[] columnWidths = {10f, 10f, 10f, 10f, 10f, 10f};
        table.setWidths(columnWidths);
        table.addCell(titleOfColumns("Description"));
        table.addCell(titleOfColumns("Quantity"));
        table.addCell(titleOfColumns("Price"));
        table.addCell(titleOfColumns("Net Value"));
        table.addCell(titleOfColumns("Vat rate"));
        table.addCell(titleOfColumns("Gross Value"));
        return table;
    }

    private PdfPTable getTableEntriesDescription(List<InvoiceEntry> entries) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setSpacingBefore(10f);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10f);
        float[] columnWidths = {10f, 10f, 10f, 10f, 10f, 10f};
        table.setWidths(columnWidths);
        for (int i = 0; i < entries.size(); i++) {
            table.addCell(entries.get(i).getDescription());
            table.addCell(String.valueOf(entries.get(i).getQuantity()));
            table.addCell(String.valueOf(entries.get(i).getPrice()));
            table.addCell(String.valueOf(entries.get(i).getNetValue()));
            table.addCell(String.valueOf(entries.get(i).getVatRate().getValue()));
            table.addCell(String.valueOf(entries.get(i).getGrossValue()));
        }
        return table;
    }

    private Chunk getTitle(Invoice invoice) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("Invoice number " + invoice.getNumber(), font);
        return chunk;
    }

    private PdfPCell titleOfColumns(String titleOfColumn) {
        PdfPCell cell = new PdfPCell(new Paragraph(titleOfColumn));
        cell.setBorderColor(BaseColor.GRAY);
        cell.setPaddingLeft(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
