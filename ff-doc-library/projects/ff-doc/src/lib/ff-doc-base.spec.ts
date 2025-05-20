import { FFDocBase, Filters } from './ff-doc-base';
import { FFDocJson } from './frankdoc.types';

const labels: FFDocJson['labels'] = {
  Components: ['Senders', 'Listeners'],
  EIP: ['Listener', 'Endpoint'],
};

const emptyFilters: Filters = {
  Components: { Senders: [], Listeners: [] },
  EIP: { Listener: [], Endpoint: [] },
};

const filledFilters: Filters = {
  Components: { Senders: ['FixedResultSender'], Listeners: ['RestListener'] },
  EIP: { Listener: ['RestListener'], Endpoint: ['FixedResultSender'] },
};

const elementNames: FFDocJson['elementNames'] = {
  FixedResultSender: {
    labels: {
      Components: 'Senders',
      EIP: 'Endpoint',
    },
    className: 'org.frankframework.senders.FixedResultSender',
  },
  RestListener: {
    labels: {
      Components: 'Listeners',
      EIP: 'Listener',
    },
    className: 'org.frankframework.http.RestListener',
  },
};

class TestFFDoc extends FFDocBase {
  runTests(): void {
    it('should get filters list from ff-doc json labels', () => {
      expect(this.getFiltersFromLabels(labels)).toEqual(emptyFilters);
    });

    it('should assign elements to filters', () => {
      const filters = this.getFiltersFromLabels(labels);
      expect(this.assignFrankDocElementsToFilters(filters, elementNames)).toEqual(filledFilters);
    });

    /* What do we want to test here?
     * Doesn't make sense to just check if the function creates an object with the elements,
     * but to also check if the elements contain the right properties.
     * Maybe also caching whenever that's implemented?
     */
    it("should convert json 'elements' & 'elementNames' to XMLElements");
  }
}

describe('FFDocBase', () => new TestFFDoc().runTests());
