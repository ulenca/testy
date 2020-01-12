import { Injectable } from '@angular/core';
import { HttpClient,  HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';

export interface InvoiceInterface {
  id: number;
  number: string;
}

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  apiURL = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  getInvoices(): Observable<InvoiceInterface> {
    return this.http.get<InvoiceInterface>(this.apiURL + '/invoices')
    .pipe(
      retry(1)
    );
  }
}
