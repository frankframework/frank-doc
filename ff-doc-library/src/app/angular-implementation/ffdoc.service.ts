import { Injectable } from '@angular/core';
import { NgFFDoc } from 'ff-doc';

@Injectable({
  providedIn: 'root',
})
export class FFDocService {
  private jsonInitialized = false;
  private jsonUrl = '/assets/example-ffdoc.json';

  private ffDoc: NgFFDoc = new NgFFDoc();

  // this would normally be done in app component but for this polyframework example a service is better
  getFFDoc(): NgFFDoc {
    if (!this.jsonInitialized) {
      this.ffDoc.initialize(this.jsonUrl);
    }
    return this.ffDoc;
  }
}
