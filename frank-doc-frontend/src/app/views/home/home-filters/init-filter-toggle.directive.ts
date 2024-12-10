import { Directive, inject, Input, OnChanges } from '@angular/core';
import { CheckboxComponent } from '@frankframework/angular-components';
import { ElementLabels } from '../../../frankdoc.types';

@Directive({
  selector: 'ff-checkbox[initFilterToggle]',
  standalone: true,
})
export class InitFilterToggleDirective implements OnChanges {
  @Input({ required: true, alias: 'initFilterToggle' }) selectedFilterLabels!: ElementLabels;
  @Input({ required: true }) initFilterName!: string;
  @Input({ required: true }) initLabelName!: string;

  private checkbox: CheckboxComponent = inject(CheckboxComponent);

  ngOnChanges(): void {
    this.checkbox.checked = this.selectedFilterLabels[this.initFilterName]?.includes(this.initLabelName) ?? false;
  }
}
