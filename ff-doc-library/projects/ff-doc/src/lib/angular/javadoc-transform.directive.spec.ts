import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { JavadocTransformDirective } from './javadoc-transform.directive';
import { Elements } from '../ff-doc-base';

@Component({
  template: `<span
    *fdJavadocTransform="let text of XMLElements['FixedResultSender'].description; elements: XMLElements"
    >{{ text }}</span
  >`,
  imports: [JavadocTransformDirective],
})
class TestComponent {
  protected XMLElements: Elements = {
    FixedResultSender: {
      name: 'FixedResultSender',
      description: 'FixedResultSender, same behaviour as {@link FixedResultPipe}, but now as a ISender.',
      labels: {},
      parentElements: [],
      attributes: {},
      parametersDescription:
        'Any parameters defined on the sender will be used for replacements. Each occurrence of <code>${name-of-parameter}</code> in the file fileName will be replaced by its corresponding value-of-parameter. This works only with files, not with values supplied in attribute {@link #setReturnString(String) returnString}.',
      forwards: {},
      enums: {},
    },
  };
}

describe('Angular JavadocTransform Directive', () => {
  let fixture: ComponentFixture<TestComponent>;
  let directiveElement: HTMLFormElement;

  beforeEach(() => {
    fixture = TestBed.configureTestingModule({
      imports: [TestComponent, JavadocTransformDirective],
      declarations: [],
    }).createComponent(TestComponent);

    fixture.detectChanges();

    directiveElement = fixture.debugElement.query(By.css('span')).nativeElement;
  });

  it('should transform the javadoc', () => {
    expect(directiveElement.textContent).toEqual(
      'FixedResultSender, same behaviour as FixedResultPipe, but now as a ISender.',
    );
  });
});
