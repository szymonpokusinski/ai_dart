import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerIndex } from './player-index';

describe('PlayerIndex', () => {
  let component: PlayerIndex;
  let fixture: ComponentFixture<PlayerIndex>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerIndex]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlayerIndex);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
