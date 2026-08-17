import { NgClass } from '@angular/common';
import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Elements, ResolvedChild } from '@frankframework/doc-library-core';
import { DEFAULT_RETURN_CHARACTER } from '../../../../app.constants';
import { AppService } from '../../../../app.service';
import { NameWbrPipe } from '../../../../components/name-wbr.pipe';

@Component({
  selector: 'app-details-element-nested',
  imports: [NameWbrPipe, NgClass, RouterLink],
  templateUrl: './details-element-nested.component.html',
  styleUrl: '../details-element-options.scss',
})
export class DetailsElementNestedComponent {
  public elements = input.required<Elements>();
  public nestedElements = input<ResolvedChild[] | null>(null);

  protected readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
}
