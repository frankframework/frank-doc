import { Elements, FFDocBase, Filters } from './ff-doc-base';
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
     * Maybe also caching whenever that's implemented?
     */
    it("should convert json 'elements' & 'elementNames' to XMLElements", () => {
      const XMLElements: Elements = {
        FixedResultSender: {
          name: 'FixedResultSender',
          description: 'FixedResultSender, same behaviour as {@link FixedResultPipe}, but now as a ISender.',
          labels: {
            Components: 'Senders',
            EIP: 'Endpoint',
          },
          parent: 'org.frankframework.senders.AbstractSenderWithParameters',
          parentElements: [], // because parent elements are not found in this example json
          attributes: {
            substituteVars: {
              description: 'should values between ${ and } be resolved from the pipelinesession',
              default: 'false',
              type: 'bool',
            },
          },
          parametersDescription:
            'Any parameters defined on the sender will be used for replacements. Each occurrence of <code>${name-of-parameter}</code> in the file fileName will be replaced by its corresponding value-of-parameter. This works only with files, not with values supplied in attribute {@link #setReturnString(String) returnString}.',
          forwards: {},
          enums: {},
        },
        RestListener: {
          name: 'RestListener',
          description:
            "Listener that allows a {@link Receiver} to receive messages as a REST webservice.\n Prepends the configured URI pattern with <code>rest/</code>. When you are writing a new Frank config, you are recommended\n to use an {@link ApiListener} instead. You can find all serviced URI patterns\n in the Frank!Console: main menu item Webservice, heading Available REST Services.\n\n <p>\n Note:\n Servlets' multipart configuration expects a Content-Type of <code>multipart/form-data</code> (see http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/MultipartConfig.html).\n So do not use other multipart content types like <code>multipart/related</code>\n </p>",
          labels: {
            Components: 'Listeners',
            EIP: 'Listener',
          },
          parent: 'org.frankframework.http.PushingListenerAdapter',
          parentElements: [], // because parent elements are not found in this example json
          attributes: {
            method: {
              description: 'Method (e.g. GET or POST) to match',
            },
          },
          forwards: {},
          enums: {},
        },
      };
      expect(this.getXMLElements(ffDocJson)).toEqual(XMLElements);
    });
  }
}

describe('FFDocBase', () => new TestFFDoc().runTests());

const ffDocJson: FFDocJson = {
  metadata: {
    version: '1.2.3-EXAMPLE',
  },
  elements: {
    'org.frankframework.senders.FixedResultSender': {
      name: 'FixedResultSender',
      description: 'FixedResultSender, same behaviour as {@link FixedResultPipe}, but now as a ISender.',
      parent: 'org.frankframework.senders.AbstractSenderWithParameters',
      attributes: {
        substituteVars: {
          description: 'should values between ${ and } be resolved from the pipelinesession',
          default: 'false',
          type: 'bool',
        },
      },
      parametersDescription:
        'Any parameters defined on the sender will be used for replacements. Each occurrence of <code>${name-of-parameter}</code> in the file fileName will be replaced by its corresponding value-of-parameter. This works only with files, not with values supplied in attribute {@link #setReturnString(String) returnString}.',
    },
    'org.frankframework.http.RestListener': {
      name: 'RestListener',
      description:
        "Listener that allows a {@link Receiver} to receive messages as a REST webservice.\n Prepends the configured URI pattern with <code>rest/</code>. When you are writing a new Frank config, you are recommended\n to use an {@link ApiListener} instead. You can find all serviced URI patterns\n in the Frank!Console: main menu item Webservice, heading Available REST Services.\n\n <p>\n Note:\n Servlets' multipart configuration expects a Content-Type of <code>multipart/form-data</code> (see http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/MultipartConfig.html).\n So do not use other multipart content types like <code>multipart/related</code>\n </p>",
      parent: 'org.frankframework.http.PushingListenerAdapter',
      attributes: {
        method: {
          description: 'Method (e.g. GET or POST) to match',
        },
      },
    },
  },
  elementNames: {
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
  },
  enums: {
    'org.frankframework.extensions.esb.EsbJmsListener.MessageProtocol': {
      FF: {
        description: 'Fire &amp; Forget protocol',
      },
      RR: {
        description: 'Request-Reply protocol',
      },
    },
    'org.frankframework.http.RestListener.MediaTypes': {
      XML: {},
      JSON: {},
      TEXT: {},
    },
    'org.frankframework.http.rest.ApiListener.HttpMethod': {
      GET: {},
      PUT: {},
      POST: {},
      PATCH: {},
      DELETE: {},
      HEAD: {},
      OPTIONS: {},
    },
  },
  labels: {
    Components: ['Senders', 'Listeners'],
    EIP: ['Listener', 'Endpoint'],
  },
  types: {},
  properties: [],
  credentialProviders: {},
  servletAuthenticators: {},
};
