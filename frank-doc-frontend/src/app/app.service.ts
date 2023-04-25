import { KeyValue } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Injectable, isDevMode } from '@angular/core';
import { environment } from 'src/environments/environment';
import { BehaviorSubject, Observable, catchError, throwError } from 'rxjs';
import { AppState, Attribute, Element, FrankDocState } from './app.types';
import { FrankDoc, Group } from './frankdoc.types';

@Injectable({
  providedIn: 'root',
})
export class AppService {
  private emptyState: AppState = {
    groups: [],
    types: {},
    elements: {},
    enums: {},
    version: null,
    showDeprecatedElements: environment.showDeprecatedElements,
    showInheritance: environment.showInheritance,
  };
  private frankDocStateSource = new BehaviorSubject<AppState>(this.emptyState);
  private frankDocUrl = '/js/frankdoc.json';

  frankDoc$ = this.frankDocStateSource.asObservable();

  constructor(private http: HttpClient) {}

  private getFrankDoc(): Observable<FrankDoc> {
    return this.http.get<FrankDoc>(this.frankDocUrl);
  }

  showHideDeprecated(): void {
    this.frankDocStateSource.next({
      ...this.frankDocStateSource.value,
      showDeprecatedElements:
        !this.frankDocStateSource.value.showDeprecatedElements,
    });
  }

  showHideInheritance(): void {
    const state = this.frankDocStateSource.value;
    const updatedElement = state.element
      ? this.getOriginalOrInheritedElement(
          !state.showInheritance,
          state.element
        )
      : undefined;

    this.frankDocStateSource.next({
      ...state,
      element: updatedElement,
      showInheritance: !state.showInheritance,
    });
  }

  setGroupAndElement(group: Group, element?: Element): void {
    const state = this.frankDocStateSource.value;
    if (element) {
      if (state.showInheritance) {
        const updatedElement = this.getOriginalOrInheritedElement(
          state.showInheritance,
          element
        );
        this.frankDocStateSource.next({
          ...state,
          group,
          element: updatedElement,
        });
        return;
      }
      this.frankDocStateSource.next({ ...state, group, element: element });
      return;
    }
    this.frankDocStateSource.next({ ...state, group });
  }

  init(): void {
    this.getFrankDoc()
      .pipe(
        catchError((errorResp) => {
          const loadError =
            errorResp.data && errorResp.data.error
              ? errorResp.data.error
              : 'Unable to load Frank!Doc.json file.';

          this.frankDocStateSource.next({ ...this.emptyState, loadError });
          return throwError(() => new Error(loadError));
        })
      )
      .subscribe((data) => {
        const distinctTypes = data.types.map((t) => t.name);
        const newState: AppState = {
          version:
            data.metadata && data.metadata.version
              ? data.metadata.version
              : null,
          groups: [{ name: 'All', types: distinctTypes }, ...data.groups],
          types: data.types.reduce<FrankDocState['types']>((acc, currType) => {
            acc[currType.name] = currType.members;
            return acc;
          }, {}),
          elements: data.elements.reduce<FrankDocState['elements']>(
            (acc, currElement) => {
              acc[currElement.fullName] = currElement;
              return acc;
            },
            {}
          ),
          enums: data.enums.reduce<FrankDocState['enums']>((acc, currEnum) => {
            acc[currEnum.name] = currEnum.values;
            return acc;
          }, {}),
          showDeprecatedElements: false,
          showInheritance: true,
        };

        this.frankDocStateSource.next(newState);
        if (isDevMode()) console.log('State initialized');
      });
  }

  getGroupElements(groupTypes: string[]): string[] {
    const allTypes = this.frankDocStateSource.value.types;
    const elementNames = groupTypes.flatMap((type) => allTypes[type]);
    return [...new Set(elementNames)]; // Get distinct element names
  }

  javaDocUrlOf(fullName: string): string | null {
    return fullName.includes('.')
      ? `${environment.javadocBaseUrl}/${fullName.replaceAll('.', '/')}.html`
      : // We only have a JavaDoc URL if we have an element with a Java class. The
        // exception we handle here is <Module>.
        null;
  }

  fullNameToSimpleName(fullName: string): string {
    return fullName.slice(fullName.lastIndexOf('.') + 1);
  }

  getOriginalOrInheritedElement(
    showInheritance: boolean,
    element: Element
  ): Element {
    const state = this.frankDocStateSource.value;
    return showInheritance
      ? this.flattenElements(element) // Merge inherited elements
      : state.elements[element.fullName]; // Update the element to it's original state
  }

  flattenElements(element: Element): Element {
    if (!element.parent) return element;

    const allElements = this.frankDocStateSource.value.elements;
    const flatElement: Element = { ...element };
    const parent = allElements[flatElement.parent!];

    //Add separator where attributes inherit from
    if (parent.attributes && parent.attributes.length > 0) {
      flatElement.attributes = [...(flatElement.attributes ?? [])]; // attributes was shallowcopied, so we need to spread it in order to use push properly
      flatElement.attributes.push({ name: '', from: parent });
    }

    flatElement.attributes =
      this.copyOf(flatElement.attributes, parent.attributes, 'name') ?? [];
    flatElement.children =
      this.copyOf(flatElement.children, parent.children, 'roleName') ?? [];
    flatElement.parameters =
      this.copyOf(flatElement.parameters, parent.parameters, 'name') ?? [];
    flatElement.forwards =
      this.copyOf(flatElement.forwards, parent.forwards, 'name') ?? [];

    flatElement.parametersDescription ??= parent.parametersDescription;
    flatElement.parent = parent.parent;

    return this.flattenElements(flatElement);
  }

  copyOf<T>(
    baseAttributes: T[] | undefined,
    mergeAttributes: T[] | undefined,
    fieldName: keyof T
  ): T[] | null {
    if (baseAttributes && !mergeAttributes) return baseAttributes;
    else if (mergeAttributes && !baseAttributes) return mergeAttributes;
    else if (!baseAttributes && !mergeAttributes) return null;

    const filteredMergeAttributes = mergeAttributes!.filter(
      (attribute) =>
        !baseAttributes!.some((ba) => ba[fieldName] === attribute[fieldName])
    );
    return [...baseAttributes!, ...filteredMergeAttributes];
  }

  orderBy<K, V>(fieldName: keyof V) {
    // function modified from https://github.com/angular/angular/blob/main/packages/common/src/pipes/keyvalue_pipe.ts#L115
    return function (
      keyValueA: KeyValue<K, V>,
      keyValueB: KeyValue<K, V>
    ): number {
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
    };
  }
}
