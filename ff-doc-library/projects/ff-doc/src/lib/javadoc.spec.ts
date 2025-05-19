import { ElementClass } from './frankdoc.types';
import { getLinkData, transformAsHtml, transformAsText } from './javadoc';

const docElements: Record<string, ElementClass> = {
  FixedResultSender: {
    name: 'FixedResultSender',
    description: 'FixedResultSender, same behaviour as {@link RestListener}, but now as a ISender.',
  },
  RestListener: {
    name: 'RestListener',
    description: 'Listener that allows a {@link Receiver} to receive messages as a REST webservice.',
  },
};
describe('Javadoc', () => {
  it('should convert link taglet to LinkData', () => {
    /* {@link FixedResultSender sender} => 'FixedResultSender sender' */
    expect(getLinkData('FixedResultSender sender', docElements)).toEqual({
      href: 'FixedResultSender',
      text: 'sender',
    });

    /* {@link FixedResultSender#configure()} => 'FixedResultSender#configure()' */
    expect(getLinkData('FixedResultSender#configure()', docElements)).toEqual({
      href: 'FixedResultSender',
      text: 'FixedResultSender.configure()',
    });

    /* {@link #doPipe(Message, PipeLineSession) doPipe} => '#doPipe(Message, PipeLineSession) doPipe' */
    expect(getLinkData('#doPipe(Message, PipeLineSession) doPipe', docElements)).toEqual({
      text: 'doPipe',
    });
  });

  it('should convert description to html', () => {
    /* Uses default link transformation */
    expect(transformAsHtml(docElements['FixedResultSender'].description!, docElements, false)).toEqual([
      'FixedResultSender, same behaviour as <a href="#/RestListener">RestListener</a>, but now as a ISender.',
    ]);

    /* gives back serialised LinkData as a part so a custom link transformation can be used */
    expect(transformAsHtml(docElements['FixedResultSender'].description!, docElements, true)).toEqual([
      'FixedResultSender, same behaviour as ',
      '{"href":"RestListener","text":"RestListener"}',
      ', but now as a ISender.',
    ]);

    /* Non existent element in elements */
    expect(transformAsHtml(docElements['RestListener'].description!, docElements, false)).toEqual([
      'Listener that allows a Receiver to receive messages as a REST webservice.',
    ]);
  });

  it('should convert description to text', () => {
    expect(transformAsText(docElements['RestListener'].description!, docElements)).toEqual([
      'Listener that allows a Receiver to receive messages as a REST webservice.',
    ]);

    expect(transformAsText(docElements['FixedResultSender'].description!, docElements)).toEqual([
      'FixedResultSender, same behaviour as RestListener, but now as a ISender.',
    ]);
  });
});
