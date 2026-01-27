import { Component } from '@angular/core';
import { InitGamePlayerListComponent } from './init-game-player-list/init-game-player-list.component';
import { IonIcon } from '@ionic/angular/standalone';
import { Router } from '@angular/router';

@Component({
  selector: 'app-init-game',
  standalone: true,
  imports: [InitGamePlayerListComponent, IonIcon],
  templateUrl: './init-game.html',
  styleUrl: './init-game.scss'
})
export class InitGameComponent {
  constructor(private router: Router) { }
  startGame() {
    this.router.navigate(['/x01-game']);
  }
}
