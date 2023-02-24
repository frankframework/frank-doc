import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { BehaviorSubject, catchError, Observable, ObservableInput, Subject, throwError } from 'rxjs';
import { AppState, FrankDocState, Types } from './app.types';
import { Element, FrankDoc, Group } from './frankdoc.types';

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
    showDeprecatedElements: false,
    showInheritance: true
  }
  private frankDocStateSource = new BehaviorSubject<AppState>(this.emptyState);
  private frankDocUrl = "/js/frankdoc.json";

  frankDoc$ = this.frankDocStateSource.asObservable();

  constructor(private http: HttpClient) { }

  private getFrankDoc() {
    return this.http.get<FrankDoc>(this.frankDocUrl);
  }

  showHideDeprecated(){
    this.frankDocStateSource.next({ ...this.frankDocStateSource.value, showDeprecatedElements: !this.frankDocStateSource.value.showDeprecatedElements });
  }

  showHideInheritance() {
    this.frankDocStateSource.next({ ...this.frankDocStateSource.value, showInheritance: !this.frankDocStateSource.value.showInheritance });

    /* if ($scope.element) {
      if ($scope.showInheritance) {
        $scope.element = $scope.flattenElements($scope.element); // Merge inherited elements
      } else {
        $scope.element = $scope.elements[$scope.element.fullName]; // Update the element to it's original state
      }
    } */
  }

  setGroupAndElement(group: Group, element?: Element) {
    console.log("update", group, element)
    if(element) {
      this.frankDocStateSource.next({ ...this.frankDocStateSource.value, group, element });
      return;
    }
    this.frankDocStateSource.next({ ...this.frankDocStateSource.value, group });
  }

  init() {
    this.getFrankDoc().pipe(catchError(errorResp => {
      let loadError: string | undefined;
      if (errorResp.data && errorResp.data.error)
        loadError = errorResp.data.error;
      else
        loadError = "Unable to load Frank!Doc.json file.";
      this.frankDocStateSource.next({ ...this.emptyState, loadError });
      return throwError(() => new Error(loadError));
    })).subscribe(data => {
      const distinctTypes = data.types.map(t => t.name);
      const newState: AppState = {
        version: (data.metadata && data.metadata.version) ? data.metadata.version : null,
        groups: [{ name: "All", types: distinctTypes }, ...data.groups],
        types: data.types.reduce<FrankDocState["types"]>((acc, currType) => {
          acc[currType.name] = currType.members;
          return acc;
        }, {}),
        elements: data.elements.reduce<FrankDocState["elements"]>((acc, currElement) => {
          acc[currElement.fullName] = currElement;
          return acc;
        }, {}),
        enums: data.enums.reduce<FrankDocState["enums"]>((acc, currEnum) => {
          acc[currEnum.name] = currEnum.values;
          return acc;
        }, {}),
        showDeprecatedElements: false,
        showInheritance: true
      }

      this.frankDocStateSource.next(newState);
      if (isDevMode())
        console.log("State initialized");
    });
  }

  getGroupElements(groupTypes: string[]) {
    const allTypes = this.frankDocStateSource.value.types;
    const elementNames = groupTypes.flatMap(type => allTypes[type]);
    return [...new Set(elementNames)]; // Get distinct element names
  }

  javaDocUrlOf(fullName: string) {
    if (fullName.includes(".")) {
      return 'https://javadoc.frankframework.org/' + fullName.replaceAll(".", "/") + '.html';
    } else {
      // We only have a JavaDoc URL if we have an element with a Java class. The
      // exception we handle here is <Module>.
      return null;
    }
  }
}
