import { CommonModule } from '@angular/common';
import { IonicModule } from '@ionic/angular';
import { Router } from '@angular/router';
import { addIcons } from 'ionicons';
import {
  backspaceOutline,
  checkmarkCircle,
  trophyOutline,
  refreshOutline,
  closeOutline
} from 'ionicons/icons';
import {Component} from '@angular/core';

@Component({
  selector: 'app-x01-game',
  standalone: true,
  imports: [CommonModule, IonicModule],
  templateUrl: './x01-game.html',
  styleUrls: ['./x01-game.scss']
})
export class X01GameComponent {
  readonly scoreButtons: number[] = [...Array.from({ length: 20 }, (_, i) => i + 1), 25];

  activeTab: 'keypad' | 'camera' = 'keypad';

  // Mock danych graczy
  players = [
    { name: 'Gracz 1 (Ty)', score: 342, avg: 45.2, darts: 12, currentDarts: ['T20', '-', '-'], currentSum: 60 },
    { name: 'Marek', score: 501, avg: 38.5, darts: 9, currentDarts: ['-', '-', '-'], currentSum: 0 },
    { name: 'Ania', score: 501, avg: 41.0, darts: 9, currentDarts: ['-', '-', '-'], currentSum: 0 },
    { name: 'Sąsiad', score: 215, avg: 55.2, darts: 15, currentDarts: ['-', '-', '-'], currentSum: 0 },
    { name: 'Bogdan', score: 501, avg: 32.1, darts: 6, currentDarts: ['-', '-', '-'], currentSum: 0 }
  ];

  constructor(private router: Router) {
    addIcons({ backspaceOutline, checkmarkCircle, trophyOutline, refreshOutline, closeOutline });
  }

  exitGame() {
    this.router.navigate(['/home']);
  }
}
