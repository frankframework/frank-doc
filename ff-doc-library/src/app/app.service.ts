import { inject, Injectable, Signal } from '@angular/core';
import { FFDoc, FrankDoc } from 'ff-doc';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AppService {
  private readonly http: HttpClient = inject(HttpClient);
  private readonly ffDoc = new FFDoc('', this.http);

  initializeDoc(): void {
    this.ffDoc.initialize();
  }

  getFrankDoc(): Signal<FrankDoc | null> {
    return this.ffDoc.frankDoc;
  }
}
