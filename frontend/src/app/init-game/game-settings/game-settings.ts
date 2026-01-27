import { CommonModule } from '@angular/common';
import {Component} from '@angular/core';

@Component({
  selector: 'app-game-settings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game-settings.html',
  styleUrl: './game-settings.scss'
})
export class GameSettingsComponent {
  // Stan rozwijania list
  isPointsOpen = false;
  isCheckoutOpen = false;

  // Wybrane wartości (defaulty)
  selectedPoints = '501';
  selectedCheckout = 'Double Out';

  // Opcje
  pointsOptions = ['301', '501', '701', '901'];
  checkoutOptions = ['Straight Out', 'Double Out', 'Master Out'];

  selectPoints(val: string) {
    this.selectedPoints = val;
    this.isPointsOpen = false;
  }

  selectCheckout(val: string) {
    this.selectedCheckout = val;
    this.isCheckoutOpen = false;
  }
}
