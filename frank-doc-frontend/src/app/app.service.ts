import { Injectable, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
// eslint-disable-next-line unicorn/prevent-abbreviations
import { FrankDoc } from './frankdoc.types';

export type ReferenceOptions = {
  darkmode: boolean;
};

@Injectable({
  providedIn: 'root',
})
export class AppService {
  readonly frankDoc: WritableSignal<FrankDoc | null> = signal(null);
  readonly darkmode: WritableSignal<boolean> = signal(false);

  constructor(private http: HttpClient) {}

  getFrankDoc(): Observable<FrankDoc> {
    return this.http.get<FrankDoc>(`${environment.frankDocUrl}?cache=${Date.now()}`);
  }
}
