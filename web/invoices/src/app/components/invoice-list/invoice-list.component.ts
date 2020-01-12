import { Component, OnInit } from '@angular/core';
import { InvoiceService } from '../../services/invoice-service.service';

@Component({
  selector: 'app-invoice-list',
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./invoice-list.component.css']
})
export class InvoiceListComponent implements OnInit {

  Invoice: any = [];

  constructor(public invoiceService: InvoiceService) { }

  ngOnInit() {
    this.loadInvoices();
  }

  loadInvoices() {
    return this.invoiceService.getInvoices().subscribe((data: {}) => {
      this.Invoice = data;
    });
  }
}
