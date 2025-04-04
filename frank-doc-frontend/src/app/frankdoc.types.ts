type FrankDocBase = {
  metadata: Metadata;
  types: TypeElement[];
  enums: Enum[];
  labels: Label[];
  properties: Property[];
  credentialProviders: CredentialProvider[];
  servletAuthenticators: ServletAuthenticator[];
};

export type FrankDoc = FrankDocBase & {
  elements: Record<string, Element>;
};

export type RawFrankDoc = FrankDocBase & {
  elements: Record<string, RawElement>;
};

type ElementBase = {
  name: string;
  abstract?: boolean;
  description?: string;
  parent?: string;
  attributes?: Attribute[];
  children?: Child[];
  forwards?: ParsedTag[];
  deprecated?: DeprecationInfo;
  parameters?: ParsedTag[];
  parametersDescription?: string;
  notes?: Note[];
  links?: Link[];
};

export type Element = ElementBase & {
  className: string;
  labels?: ElementLabels; // maybe make this smarter and have key be based on Label (type) name
};

export type RawElement = ElementBase & {
  fullName: string;
  elementNames: {
    name: string;
    labels: ElementLabels;
  }[];
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

export type CredentialProvider = {
  name: string;
  fullName: string;
  description?: string;
};

export type ServletAuthenticator = {
  name: string;
  fullName: string;
  description?: string;
  methods?: Method[];
};

export type Note = {
  type: 'INFO' | 'WARNING' | 'DANGER' | 'TIP';
  value: string;
};

export type Link = {
  label: string;
  url: string;
};

export type Method = {
  name: string;
  description?: string;
};
