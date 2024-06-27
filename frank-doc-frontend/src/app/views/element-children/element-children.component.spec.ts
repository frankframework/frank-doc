import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppService } from 'src/app/app.service';

import { ElementChildrenComponent } from './element-children.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ElementChildrenComponent', () => {
  let component: ElementChildrenComponent;
  let fixture: ComponentFixture<ElementChildrenComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [ElementChildrenComponent],
    imports: [],
    providers: [AppService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
}).compileComponents();

    fixture = TestBed.createComponent(ElementChildrenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
