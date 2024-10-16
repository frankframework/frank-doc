import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
// eslint-disable-next-line unicorn/prevent-abbreviations
import { FrankDoc } from './frankdoc.types';

@Injectable({
  providedIn: 'root',
})
export class AppService {
  constructor(private http: HttpClient) {}

  private getFrankDoc(): Observable<FrankDoc> {
    return this.http.get<FrankDoc>(`${environment.frankDocUrl}?cache=${Date.now()}`);
  }
}
