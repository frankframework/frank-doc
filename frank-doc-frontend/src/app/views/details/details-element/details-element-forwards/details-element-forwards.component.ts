import { Component, computed, inject, input } from '@angular/core';
import { ElementProperty, Elements } from '@frankframework/doc-library-core';
import { JavadocTransformDirective } from '@frankframework/doc-library-ng';
import { DEFAULT_RETURN_CHARACTER } from '../../../../app.constants';
import { AppService } from '../../../../app.service';
import { NameWbrPipe } from '../../../../components/name-wbr.pipe';

@Component({
  selector: 'app-details-element-forwards',
  imports: [JavadocTransformDirective, NameWbrPipe],
  templateUrl: './details-element-forwards.component.html',
  styleUrl: '../details-element-options.scss',
})
export class DetailsElementForwardsComponent {
  public elements = input.required<Elements>();
  public forwards = input.required<Record<string, ElementProperty>>();
  public inheritedForwards = input<Record<string, ElementProperty>>({});
  protected allForwards = computed<Record<string, ElementProperty>>(() => ({
    ...this.inheritedForwards(),
    ...this.forwards(),
  }));

  protected readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly getRecordEntries = this.appService.getRecordEntries;
}
