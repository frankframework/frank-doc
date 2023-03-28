import { KeyValue } from '@angular/common';
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

  constructor(private http: HttpClient) {}

  private getFrankDoc() {
    return this.http.get<FrankDoc>(this.frankDocUrl);
  }

  showHideDeprecated(){
    this.frankDocStateSource.next({ ...this.frankDocStateSource.value, showDeprecatedElements: !this.frankDocStateSource.value.showDeprecatedElements });
  }

  showHideInheritance() {
    const state = this.frankDocStateSource.value;
    const updatedElement = this.elementInheritance(!state.showInheritance, state.element);

    this.frankDocStateSource.next({ ...state, element: updatedElement, showInheritance: !state.showInheritance });
  }

  setGroupAndElement(group: Group, element?: Element) {
    const state = this.frankDocStateSource.value;
    if(element) {
      if (state.showInheritance){
        const updatedElement = this.elementInheritance(state.showInheritance, element);
        this.frankDocStateSource.next({ ...state, group, element: updatedElement });
        return;
      }
      this.frankDocStateSource.next({ ...state, group, element: element });
      return;
    }
    this.frankDocStateSource.next({ ...state, group });
  }

  init() {
    this.getFrankDoc().pipe(catchError(errorResp => {
      const loadError = (errorResp.data && errorResp.data.error) ? errorResp.data.error : "Unable to load Frank!Doc.json file.";

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
    return fullName.slice(fullName.lastIndexOf(".") + 1)
  }

  elementInheritance(showInheritance: boolean, element?: Element){
    const state = this.frankDocStateSource.value;
    if (element) {
      if (showInheritance) {
        return this.flattenElements(element); // Merge inherited elements
      }
      return state.elements[element.fullName]; // Update the element to it's original state
    }
    return element;
  }

  flattenElements(element: Element): Element {
    if (element.parent) {
      const allElements = this.frankDocStateSource.value.elements;
      const el: Element = {...element}
      const parent = allElements[el.parent!];

      //Add separator where attributes inherit from
      if (parent.attributes && parent.attributes.length > 0) {
        if (!el.attributes) { el.attributes = []; } //Make sure an array exists

        el.attributes = [...el.attributes]; // attributes was shallowcopied, so we need to spread it in order to use push properly
        el.attributes.push({ name: "", from: parent });
      }

      el.attributes = this.copyOf(el.attributes, parent.attributes, 'name') || [];
      el.children = this.copyOf(el.children, parent.children, 'roleName') || [];
      el.parameters = this.copyOf(el.parameters, parent.parameters, 'name') || [];
      el.forwards = this.copyOf(el.forwards, parent.forwards, 'name') || [];

      if (!el.parametersDescription && parent.parametersDescription) {
        el.parametersDescription = parent.parametersDescription;
      }

      el.parent = parent.parent || undefined;

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

    const filteredMergeAttrs = mergeAttrs!.filter(attr => !baseAttrs!.some(ba => ba[fieldName] === attr[fieldName]));
    return [...baseAttrs!, ...filteredMergeAttrs];
  }

  orderBy<K, V>(fieldName: keyof V) {
    // function modified from https://github.com/angular/angular/blob/main/packages/common/src/pipes/keyvalue_pipe.ts#L115
    return function(keyValueA: KeyValue<K, V>, keyValueB: KeyValue<K, V>) {
      const a = keyValueA.value[fieldName];
      const b = keyValueB.value[fieldName];
      // if same exit with 0;
      if (a === b) return 0;
      // make sure that undefined are at the end of the sort.
      if (a === undefined) return 1;
      if (b === undefined) return -1;
      // make sure that nulls are at the end of the sort.
      if (a === null) return 1;
      if (b === null) return -1;
      if (typeof a == 'string' && typeof b == 'string') {
        return a < b ? -1 : 1;
      }
      if (typeof a == 'number' && typeof b == 'number') {
        return a - b;
      }
      if (typeof a == 'boolean' && typeof b == 'boolean') {
        return a < b ? -1 : 1;
      }
      // `a` and `b` are of different types. Compare their string values.
      const aString = String(a);
      const bString = String(b);
      return aString == bString ? 0 : (aString < bString ? -1 : 1);
    }
  }
}
