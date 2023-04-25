import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppService } from 'src/app/app.service';

import { AttributeDescriptionComponent } from './attribute-description.component';

describe('AttributeDescriptionComponent', () => {
  let component: AttributeDescriptionComponent;
  let fixture: ComponentFixture<AttributeDescriptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AppService],
      declarations: [AttributeDescriptionComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AttributeDescriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
