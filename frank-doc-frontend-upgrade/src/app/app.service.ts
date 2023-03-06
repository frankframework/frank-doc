import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { BehaviorSubject, catchError, throwError } from 'rxjs';
import { AppState, Attribute, Element, FrankDocState } from './app.types';
import { FrankDoc, Group } from './frankdoc.types';

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

  fullNameToSimpleName(fullName: string) {
    return fullName.substring(fullName.lastIndexOf(".") + 1)
  }

  flattenElements(element: Element): Element {
    if (element.parent) {
      const allElements = this.frankDocStateSource.value.elements;
      const el: Element = {...element}
      const parent = allElements[element.parent];

      //Add separator where attributes inherit from
      if (parent.attributes && parent.attributes.length > 0) {
        if (!el.attributes) { el.attributes = []; } //Make sure an array exists

        el.attributes.push({ name: "", from: parent });
      }

      el.attributes = this.copyOf(el.attributes, parent.attributes, 'name') || [];
      el.children = this.copyOf(el.children, parent.children, 'roleName') || [];
      el.parameters = this.copyOf(el.parameters, parent.parameters, 'name') || [];
      el.forwards = this.copyOf(el.forwards, parent.forwards, 'name') || [];

      if (!el.parametersDescription && parent.parametersDescription) {
        el.parametersDescription = parent.parametersDescription;
      }
      if (parent.parent) {
        el.parent = parent.parent;
      } else {
        el.parent = undefined;
      }
      return this.flattenElements(el);
    }

    return element;
  }

  copyOf<T>(baseAttrs: T[] | undefined, mergeAttrs: T[] | undefined, fieldName: keyof T) {
    if (baseAttrs && !mergeAttrs)
      return baseAttrs;
    else if (mergeAttrs && !baseAttrs)
      return mergeAttrs;
    else if (!baseAttrs && !mergeAttrs)
      return null;

    const filteredMergeAttrs = mergeAttrs!.filter(attr => !baseAttrs!.find(ba => ba[fieldName] === attr[fieldName]));
    return [...baseAttrs!, ...filteredMergeAttrs];
  }
}
