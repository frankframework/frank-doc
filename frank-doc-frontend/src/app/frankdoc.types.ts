export interface FrankDoc {
  metadata: Metadata;
  types: TypeElement[];
  elements: Record<string, Element>;
  enums: Enum[];
  labels: Label[];
  properties: Property[];
}

export type Element = {
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
  labels?: ElementLabels; // maybe make this smarter and have key be based on Label (type) name
  notes?: Note[];
  links?: Link[];
};

export type Attribute = {
  name: string;
  mandatory?: boolean;
  describer?: string;
  description?: string;
  type?: string;
  default?: string;
  deprecated?: DeprecationInfo;
  enum?: string;
};

export type Child = {
  multiple: boolean;
  roleName: string;
  description?: string;
  type?: string;
  deprecated?: boolean;
  mandatory?: boolean;
};

export type ParsedTag = {
  name: string;
  description?: string;
};

export type ElementLabels = Record<string, string[]>;

export type Enum = {
  name: string;
  values: Value[];
};

export type Value = {
  label: string;
  description?: string;
  deprecated?: boolean;
};

export type Group = {
  name: string;
  types: string[];
};

export type Metadata = {
  version: string;
};

export type TypeElement = {
  name: string;
  members: string[];
};

export type DeprecationInfo = {
  forRemoval: boolean;
  since?: string;
  description?: string;
};

export type Label = {
  label: string;
  values: string[];
};

export type Property = {
  name: string;
  properties: {
    name: string;
    description: string;
    defaultValue?: string;
    flags?: string[];
  }[];
};

export type Note = {
  type: 'INFO' | 'WARNING' | 'DANGER' | 'TIP';
  value: string;
};

export type Link = {
  label: string;
  url: string;
};
