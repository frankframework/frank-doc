import { Attribute, ElementClass, ElementProperty, EnumValue, FFDocJson } from './frankdoc.types';

export type InheritedParentElementProperties<T> = {
  parentElementName: string;
  properties: Record<string, T>;
};

type InheritedPropertiesExtras = {
  enums: Record<string, EnumValue>;
  parentElements: string[];
};

export type InheritedProperties = {
  attributesRequired: InheritedParentElementProperties<Attribute>[];
  attributesOptional: InheritedParentElementProperties<Attribute>[];
  forwards: Record<string, ElementProperty>;
} & InheritedPropertiesExtras;

export type ElementClassWithInheritedProperties = ElementClass & InheritedPropertiesExtras;

function filterUsedEnums(attributes: Record<string, Attribute>, enums: FFDocJson['enums']): Record<string, EnumValue> {
  const filteredEnums: Record<string, EnumValue> = {};
  for (const attribute of Object.values(attributes)) {
    if (attribute.enum) {
      const enumType = enums[attribute.enum];
      if (enumType && !filteredEnums[attribute.enum]) filteredEnums[attribute.enum] = enumType;
    }
  }
  return filteredEnums;
}

// eslint-disable-next-line sonarjs/no-commented-code
/*export function getElementByLabel(
  labelGroup: string,
  label: string,
  name: string,
  elements: Elements,
): ElementDetails | null {
  return (
    Object.values(elements).find((element) => element.name === name && element.labels?.[labelGroup]?.includes(label)) ??
    null
  );
}*/

export function getInheritedProperties(
  element: ElementClass,
  elements: FFDocJson['elements'],
  enums: FFDocJson['enums'],
): InheritedProperties {
  const initialInheritedProperties: InheritedProperties = {
    attributesRequired: [],
    attributesOptional: [],
    forwards: {},
    enums: {},
    parentElements: [],
  };

  if (!element.parent) return initialInheritedProperties;
  const parentElement = elements[element.parent];
  if (!parentElement) return initialInheritedProperties;
  const inheritedProperties = getInheritedProperties(parentElement, elements, enums);
  inheritedProperties.parentElements.unshift(parentElement.name);

  if (parentElement.attributes) {
    // ES2024 has the Object.groupBy function which could make this a lot better
    const { required: attributesRequired, optional: attributesOptional } = Object.entries(
      parentElement.attributes,
    ).reduce<{
      required: Record<string, Attribute>;
      optional: Record<string, Attribute>;
    }>(
      (collection, [name, attribute]) => {
        collection[attribute.mandatory === true ? 'required' : 'optional'][name] = attribute;
        return collection;
      },
      { required: {}, optional: {} },
    );
    if (Object.values(attributesRequired).length > 0) {
      inheritedProperties.attributesRequired.unshift({
        parentElementName: parentElement.name,
        properties: attributesRequired,
      });
    }
    if (Object.values(attributesOptional).length > 0) {
      inheritedProperties.attributesOptional.unshift({
        parentElementName: parentElement.name,
        properties: attributesOptional,
      });
    }
    inheritedProperties.enums = { ...inheritedProperties.enums, ...filterUsedEnums(parentElement.attributes, enums) };
  }

  if (parentElement.forwards)
    inheritedProperties.forwards = { ...inheritedProperties.forwards, ...parentElement.forwards };

  return inheritedProperties;
}

export function addInheritedPropertiesToElement(
  element: ElementClass,
  elements: FFDocJson['elements'],
  enums: FFDocJson['enums'],
): ElementClassWithInheritedProperties {
  const inheritedProperties = getInheritedProperties(element, elements, enums);
  return {
    ...element,
    parentElements: inheritedProperties.parentElements,
    attributes: {
      ...flattenInheritedParentElementProperties(inheritedProperties.attributesOptional),
      ...flattenInheritedParentElementProperties(inheritedProperties.attributesRequired),
      ...element.attributes,
    },
    forwards: { ...inheritedProperties.forwards, ...element.forwards },
    enums: inheritedProperties.enums,
  };
}

export function flattenInheritedParentElementProperties<T>(
  inheritedProperties: InheritedParentElementProperties<T>[],
): Record<string, T> {
  let flattenedProperties: Record<string, T> = {};
  for (const inheritedProperty of inheritedProperties) {
    flattenedProperties = { ...inheritedProperty.properties, ...flattenedProperties };
  }
  return flattenedProperties;
}
