import { FFDocJson } from './frankdoc.types';
import { Elements, FFDocBase, Filters } from './ff-doc-base';

export class FFDoc extends FFDocBase {
  private _ffDoc: FFDocJson | null = null;
  private _elements: Elements | null = null;
  private _filters: Filters = {};

  get ffDoc(): Readonly<FFDocJson | null> {
    return this._ffDoc;
  }

  get elements(): Readonly<Elements | null> {
    return this._elements;
  }

  get filters(): Readonly<Filters> {
    return this._filters;
  }

  async initialize(jsonUrl: string): Promise<void> {
    const ffDocJson = await this.fetchJson(jsonUrl);
    this._ffDoc = ffDocJson;
    this._filters = this.assignFrankDocElementsToFilters(
      this.getFiltersFromLabels(ffDocJson.labels),
      ffDocJson.elementNames,
    );
    this._elements = this.getXMLElements(ffDocJson);
  }

  private async fetchJson(url: string): Promise<FFDocJson> {
    const response = await fetch(url);
    return await response.json();
  }
}
