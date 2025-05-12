export type FFDocJson = {
  metadata: Metadata;
  types: Record<string, string[]>;
  elements: Record<string, ElementClass>;
  elementNames: Record<string, ElementInfo>;
  enums: Record<string, EnumValue>;
  labels: Record<string, string[]>;
  properties: Property[];
  credentialProviders: Record<string, CredentialProvider>;
  servletAuthenticators: Record<string, ServletAuthenticator>;
};

export type Metadata = {
  version: string;
};

export type ElementClass = {
  name: string;
  abstract?: boolean;
  deprecated?: DeprecationInfo;
  description?: string;
  parent?: string;
  attributes?: Record<string, Attribute>;
  children?: Child[];
  forwards?: Record<string, ElementProperty>;
  parameters?: Record<string, ElementProperty>;
  parametersDescription?: string;
  notes?: Note[];
  links?: Link[];
};

export type DeprecationInfo = {
  forRemoval: boolean;
  since?: string;
  description?: string;
};

export type Attribute = {
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

export type ElementProperty = {
  description?: string;
};

export type Note = {
  type: 'INFO' | 'WARNING' | 'DANGER' | 'TIP';
  value: string;
};

export type Link = {
  label: string;
  url: string;
};

export type ElementInfo = {
  labels: Record<string, string>;
  className: string;
};

export type EnumValue = {
  description?: string;
  deprecated?: boolean;
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
  fullName: string;
  description?: string;
};

export type ServletAuthenticator = {
  fullName: string;
  description?: string;
  methods?: {
    name: string;
    description?: string;
  }[];
};
