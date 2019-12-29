import { TestBed } from '@angular/core/testing';

import { InvoiceService } from './invoice-service.service';

describe('InvoiceServiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: InvoiceService = TestBed.get(InvoiceService);
    expect(service).toBeTruthy();
  });
});
