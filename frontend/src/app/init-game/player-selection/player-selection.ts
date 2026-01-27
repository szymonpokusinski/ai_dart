import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-player-selection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './player-selection.html',
  styleUrl: './player-selection.scss'
})
export class PlayerSelectionComponent {
  players = [
    { name: 'Player 1', rating: '1250' },
    { name: 'Player 2', rating: '1100' }
  ];
}
