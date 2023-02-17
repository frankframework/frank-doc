import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, ObservableInput, Subject, throwError } from 'rxjs';
import { AppState } from './app.types';
import { FrankDoc, Group, TypeElement, Element, Enum } from './frankdoc.types';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private emptyState: AppState = {
    groups: [],
    types: {},
    elements: {},
    enums: {},
    version: null,
  }
  private appStateSource = new BehaviorSubject<AppState>(this.emptyState);
  private frankDocUrl = "/assets/frankdoc.json";

  appState$ = this.appStateSource.asObservable();

  constructor(private http: HttpClient) { }

  init() {
    this.getFrankDoc().pipe(catchError(errorResp => {
      let loadError: string | undefined;
      if (errorResp.data && errorResp.data.error)
        loadError = errorResp.data.error;
      else
        loadError = "Unable to load Frank!Doc.json file.";
      this.appStateSource.next({ ...this.emptyState, loadError });
      return throwError(() => new Error(loadError));
    })).subscribe(data => {
      const distinctTypes = data.types.map(t => t.name);
      const newState: AppState = {
        version: (data.metadata && data.metadata.version) ? data.metadata.version : null,
        groups: [{ name: "All", types: distinctTypes }, ...data.groups],
        types: data.types.reduce<AppState["types"]>((acc, currType) => {
          acc[currType.name] = currType.members;
          return acc;
        }, {}),
        elements: data.elements.reduce<AppState["elements"]>((acc, currElement) => {
          acc[currElement.fullName] = currElement;
          return acc;
        }, {}),
        enums: data.enums.reduce<AppState["enums"]>((acc, currEnum) => {
          acc[currEnum.name] = currEnum.values;
          return acc;
        }, {})
      };

      this.appStateSource.next(newState);
    });
  }

  private getFrankDoc() {
    return this.http.get<FrankDoc>(this.frankDocUrl);
  }
}
