import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Comision } from './comision';

describe('Comision', () => {
  let component: Comision;
  let fixture: ComponentFixture<Comision>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Comision]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Comision);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
