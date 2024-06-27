import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppService } from 'src/app/app.service';

import { AttributeDescriptionComponent } from './attribute-description.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('AttributeDescriptionComponent', () => {
  let component: AttributeDescriptionComponent;
  let fixture: ComponentFixture<AttributeDescriptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    declarations: [AttributeDescriptionComponent],
    imports: [],
    providers: [AppService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
}).compileComponents();

    fixture = TestBed.createComponent(AttributeDescriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
