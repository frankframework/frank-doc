export interface FrankDoc {
  metadata: Metadata;
  groups: Group[];
  types: TypeElement[];
  elements: Element[];
  enums: Enum[];
}

export interface Element {
  name: string;
  fullName: string;
  abstract?: boolean;
  description?: string;
  parent?: string;
  elementNames: string[];
  attributes?: Attribute[];
  children?: Child[];
  forwards?: ParsedTag[];
  deprecated?: DeprecationInfo;
  parameters?: ParsedTag[];
  parametersDescription?: string;
}

export interface Attribute {
  name: string;
  mandatory?: boolean;
  describer?: string;
  description?: string;
  type?: DataType;
  default?: string;
  deprecated?: DeprecationInfo;
  enum?: string;
}

export enum DataType {
  Bool = 'bool',
  Int = 'int',
}

export interface Child {
  multiple: boolean;
  roleName: string;
  description?: string;
  type?: string;
  deprecated?: boolean;
  mandatory?: boolean;
}

export interface ParsedTag {
  name: string;
  description?: string;
}

export interface Enum {
  name: string;
  values: Value[];
}

export interface Value {
  label: string;
  description?: string;
  deprecated?: boolean;
}

export interface Group {
  name: string;
  types: string[];
}

export interface Metadata {
  version: string;
}

export interface TypeElement {
  name: string;
  members: string[];
}

export interface DeprecationInfo {
  forRemoval: boolean;
  since?: string;
  description?: string;
}
