import { Group, Element, Value } from "./frankdoc.types";

export interface FrankDocState {
    groups: Group[];
    types: Types;
    elements: Elements;
    enums: Enums;
}

export interface AppState extends FrankDocState {
    version: string | null;
    loadError?: string;
    showDeprecatedElements: boolean;
    showInheritance: boolean;
    group?: Group;
    element?: Element;
}

export interface Types {
    [index: string]: string[];
}

export interface Elements {
    [index: string]: Element;
}

export interface Enums {
    [index: string]: Value[];
}
