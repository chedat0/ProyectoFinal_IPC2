import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistroFreelancer } from './registro-freelancer';

describe('RegistroFreelancer', () => {
  let component: RegistroFreelancer;
  let fixture: ComponentFixture<RegistroFreelancer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistroFreelancer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistroFreelancer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
