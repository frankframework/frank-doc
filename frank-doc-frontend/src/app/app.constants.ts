import { IFuseOptions } from 'fuse.js';
import { Element } from './frankdoc.types';

export const DEFAULT_RETURN_CHARACTER = '-';
export const DEFAULT_UNKNOWN_PROPERTY_GROUP = '(unnamed)';

export const fuseOptions: IFuseOptions<Element> = {
  keys: [
    {
      name: 'name',
      weight: 2,
    },
    {
      name: 'fullName',
      weight: 2,
    },
    {
      name: 'elementNames', // default weight is 1.0
    },
    {
      name: 'labels.FrankDocGroup',
    },
    {
      name: 'attributes',
      weight: 0.7,
    },
    {
      name: 'description',
      weight: 0.5,
    },
  ],
  includeScore: true,
  includeMatches: true,
  threshold: 0.2,
  minMatchCharLength: 3,
  ignoreLocation: true,
};

export const filterColours: string[] = ['#CD55EB', '#037CD4', '#00B31D'];

export const splitOnPascalCaseRegex: RegExp = /(?=[\dA-Z])/;
