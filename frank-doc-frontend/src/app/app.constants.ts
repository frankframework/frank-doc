import { IFuseOptions } from 'fuse.js';
import { ElementDetails } from '@frankframework/doc-library-core';

export const DEFAULT_RETURN_CHARACTER = '-';
export const DEFAULT_UNKNOWN_PROPERTY_GROUP = '(unnamed)';

export const fuseOptions: IFuseOptions<ElementDetails> = {
  keys: [
    {
      name: 'name',
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

export const splitOnPascalCaseRegex: RegExp = /(?=[\dA-Z])/;
