import { Component, computed, inject, Signal } from '@angular/core';
import { FFDocService } from './ffdoc.service';
import { Elements, JavadocTransformDirective } from 'ff-doc';
import { KeyValuePipe } from '@angular/common';

@Component({
  selector: 'app-angular-implementation',
  imports: [KeyValuePipe, JavadocTransformDirective],
  templateUrl: './angular-implementation.component.html',
  styleUrl: './angular-implementation.component.scss',
})
export class AngularImplementationComponent /*implements OnInit*/ {
  protected elements: Signal<Elements> = computed(() => this.ffdoc.elements() ?? {});

  private ffdocService: FFDocService = inject(FFDocService);
  private ffdoc = this.ffdocService.getFFDoc();

  // ngOnInit(): void {}
}
