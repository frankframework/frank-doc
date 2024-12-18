import { IFuseOptions } from 'fuse.js';
import { Element } from './frankdoc.types';

export const DEFAULT_RETURN_CHARACTER = '-';

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

export const styleColours: Map<string, string> = new Map([
  ['ff-color-light', '#FFFFFF'],
  ['ff-color-dark', '#1E1E1E'],
  ['ff-color-gray', '#8A8A8A'],
  ['ff-color-dark-gray', '#626262'],

  ['ff-bgcolor-white', '#FAFAFA'],
  ['ff-bgcolor-gray', '#D8D8D8'],
  ['ff-bgcolor-dark-gray', '#343434'],
  ['ff-bgcolor-yellow', '#FDC300'],

  ['ff-border-gray', '#626262'],
  ['ff-border-yellow', '#D3A200'],

  ['ff-alert-info', '#037CD4'],
  ['ff-alert-success', '#00d841'],
  ['ff-alert-warning', '#d89400'],
  ['ff-alert-error', '#DA0004'],
]);
