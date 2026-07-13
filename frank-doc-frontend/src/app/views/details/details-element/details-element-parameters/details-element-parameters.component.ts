import { Component, inject, input } from '@angular/core';
import { ElementProperty, Elements } from '@frankframework/doc-library-core';
import { JavadocTransformDirective } from '@frankframework/doc-library-ng';
import { DEFAULT_RETURN_CHARACTER } from '../../../../app.constants';
import { AppService } from '../../../../app.service';
import { NameWbrPipe } from '../../../../components/name-wbr.pipe';

@Component({
  selector: 'app-details-element-parameters',
  imports: [JavadocTransformDirective, NameWbrPipe],
  templateUrl: './details-element-parameters.component.html',
  styleUrl: '../details-element-options.scss',
})
export class DetailsElementParametersComponent {
  public elements = input.required<Elements>();
  public parameters = input<Record<string, ElementProperty>>();
  public parametersDescription = input<string>();

  protected readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly getRecordEntries = this.appService.getRecordEntries;
  protected readonly isRecordGreaterThanZero = this.appService.isRecordGreaterThanZero;
}
