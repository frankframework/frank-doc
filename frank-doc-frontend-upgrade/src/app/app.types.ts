import { Group, Element, Value } from "./frankdoc.types";

export interface AppState {
    groups: Group[];
    types: Types;
    elements: Elements;
    enums: Enums;
    version: string | null;
    loadError ?: string;
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
