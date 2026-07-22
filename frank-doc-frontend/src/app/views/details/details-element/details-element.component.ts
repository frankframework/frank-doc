import {
  Component,
  computed,
  EventEmitter,
  inject,
  Input,
  OnChanges,
  OnInit,
  Output,
  Signal,
  SimpleChanges,
  ChangeDetectionStrategy,
} from '@angular/core';
import { AlertComponent, AlertType, ChipComponent } from '@frankframework/angular-components';
import { KeyValuePipe, NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { CollapseDirective } from '../../../components/collapse.directive';
import { IconCaretComponent } from '../../../icons/icon-caret-down/icon-caret.component';
import { IconArrowRightUpComponent } from '../../../icons/icon-arrow-right-up/icon-arrow-right-up.component';
import { AppService } from '../../../app.service';
import { DEFAULT_RETURN_CHARACTER } from '../../../app.constants';
import { HasInheritedProperties } from '../details.component';
import { Title } from '@angular/platform-browser';
import { NameWbrPipe } from '../../../components/name-wbr.pipe';
import { JavadocTransformDirective, NgFFDoc } from '@frankframework/doc-library-ng';
import {
  Attribute,
  Child,
  ElementClass,
  ElementDetails,
  Elements,
  getInheritedProperties,
  groupAttributesByMandatory,
  InheritedProperties,
  Note,
  ResolvedChild,
  resolveInterfaceChildren,
} from '@frankframework/doc-library-core';
import { DetailsElementAttributesComponent } from './details-element-attributes/details-element-attributes.component';
import { DetailsElementForwardsComponent } from './details-element-forwards/details-element-forwards.component';
import { DetailsElementNestedComponent } from './details-element-nested/details-element-nested.component';
import { DetailsElementParametersComponent } from './details-element-parameters/details-element-parameters.component';
import { DetailsElementSyntaxComponent } from './details-element-syntax/details-element-syntax.component';

@Component({
  selector: 'app-details-element',
  imports: [
    ChipComponent,
    KeyValuePipe,
    RouterLink,
    AlertComponent,
    CollapseDirective,
    IconCaretComponent,
    IconArrowRightUpComponent,
    NgClass,
    NameWbrPipe,
    JavadocTransformDirective,
    DetailsElementSyntaxComponent,
    DetailsElementAttributesComponent,
    DetailsElementParametersComponent,
    DetailsElementNestedComponent,
    DetailsElementForwardsComponent,
  ],
  templateUrl: './details-element.component.html',
  changeDetection: ChangeDetectionStrategy.Eager,
  styleUrl: './details-element.component.scss',
})
export class DetailsElementComponent implements OnInit, OnChanges {
  @Input({ required: true }) element!: ElementDetails | null;
  @Output() hasInheritedProperties = new EventEmitter<HasInheritedProperties>();

  protected elements: Signal<Elements> = computed(() => this.ffDoc.elements() ?? {});
  protected types: Signal<Record<string, string[]>> = computed(() => this.ffDoc.ffDoc()?.types ?? {});
  protected attributesRequired: Record<string, Attribute> = {};
  protected attributesOptional: Record<string, Attribute> = {};
  protected allRequiredAttributes: Record<string, Attribute> = {};
  protected inheritedProperties: InheritedProperties = {
    parentElements: [],
    attributesRequired: [],
    attributesOptional: [],
    parameters: {},
    forwards: {},
    enums: {},
  };
  protected nestedElements: ResolvedChild[] | null = null;
  protected collapsedOptions = {
    attributes: false,
    parameters: false,
    children: false,
    forwards: false,
  };
  protected loading = true;

  protected readonly appService: AppService = inject(AppService);
  protected readonly DEFAULT_RETURN_CHARACTER = DEFAULT_RETURN_CHARACTER;
  protected readonly isRecordGreaterThanZero = this.appService.isRecordGreaterThanZero;
  private readonly titleService: Title = inject(Title);
  private readonly ffDoc: NgFFDoc = this.appService.getFFDoc();

  ngOnInit(): void {
    this.appService.applicationLoaded$.subscribe(() => (this.loading = false));
    this.loading = !this.appService.hasLoaded;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (!changes['element']) return;

    this.resetProperties();
    const classElement = this.getClassElement();
    if (classElement?.attributes) {
      const { required, optional } = groupAttributesByMandatory(classElement.attributes);
      this.attributesRequired = required;
      this.attributesOptional = optional;
    }
    if (classElement?.parent) {
      this.inheritedProperties = getInheritedProperties(
        classElement,
        this.ffDoc.ffDoc()?.elements ?? {},
        this.ffDoc.ffDoc()?.enums ?? {},
      );
      this.updateHasInheritedProperties();
    }
    if (classElement?.children) {
      this.nestedElements = classElement.children.map((child) => this.getNestedTypeElements(child));
    }
    this.allRequiredAttributes = groupAttributesByMandatory(this.element?.attributes ?? {});

    if (this.element?.name)
      this.titleService.setTitle(`${environment.applicationName} | ${this.element?.name ?? 'Element details'}`);
  }

  protected githubUrlOf(name: string): string {
    return `${environment.githubWikiBaseUrl}/${name}`;
  }

  protected javaDocUrlOf(fullName: string): string | null {
    return fullName.includes('.')
      ? `${environment.javadocBaseUrl}/${fullName.replaceAll('.', '/')}.html`
      : // We only have a Javadoc URL if we have an element with a Java class. The
        // exception we handle here is <Module>.
        null;
  }

  protected getAllRequiredAttributes(): Record<string, Attribute> {
    return {
      ...this.inheritedProperties.attributesRequired
        .map((item) => item.properties)
        .reduce((mergedAttributes, attributes) => ({ ...mergedAttributes, ...attributes }), {}),
      ...this.attributesRequired,
    };
  }

  protected getWarningType(type: Note['type']): AlertType {
    switch (type) {
      case 'WARNING': {
        return 'warning';
      }
      case 'DANGER': {
        return 'error';
      }
      default: {
        return 'info';
      }
    }
  }

  private getNestedTypeElements(child: Child): ResolvedChild {
    return resolveInterfaceChildren(child, this.types(), this.elements());
  }

  private getClassElement(): ElementClass | null {
    const classElements = this.ffDoc.ffDoc()?.elements;
    return classElements && this.element ? classElements[this.element?.className] : null;
  }

  private updateHasInheritedProperties(): void {
    this.hasInheritedProperties.emit({
      required: this.inheritedProperties.attributesRequired.length > 0,
      optional: this.inheritedProperties.attributesOptional.length > 0,
      parameters: this.isRecordGreaterThanZero(this.inheritedProperties.parameters),
      forwards: this.isRecordGreaterThanZero(this.inheritedProperties.forwards),
    });
  }

  private resetProperties(): void {
    this.attributesRequired = {};
    this.attributesOptional = {};
    this.allRequiredAttributes = {};
    this.nestedElements = null;
    this.inheritedProperties = {
      parentElements: [],
      attributesRequired: [],
      attributesOptional: [],
      parameters: {},
      forwards: {},
      enums: {},
    };
    this.updateHasInheritedProperties();
  }
}
