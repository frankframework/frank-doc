import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppService } from 'src/app/app.service';

import { ElementAttributesComponent } from './element-attributes.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ElementAttributesComponent', () => {
  let component: ElementAttributesComponent;
  let fixture: ComponentFixture<ElementAttributesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [ElementAttributesComponent],
    imports: [],
    providers: [AppService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
}).compileComponents();

    fixture = TestBed.createComponent(ElementAttributesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
