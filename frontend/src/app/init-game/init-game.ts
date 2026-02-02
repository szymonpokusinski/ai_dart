import { Component } from '@angular/core';
import { InitGamePlayerListComponent } from './init-game-player-list/init-game-player-list.component';
import { IonIcon } from '@ionic/angular/standalone';
import { Router } from '@angular/router';
import {GameSettingsSelectorComponent} from './game-settings/game-settings';
import {GameRequest} from '../core/models/request/game.request';
import {GameType} from '../core/models/db/game/GameType';
import {GameFinishRule} from '../core/models/db/game/GameFinishRule';
import {GameService} from '../core/service/game.service';

@Component({
  selector: 'app-init-game',
  standalone: true,
  imports: [InitGamePlayerListComponent, IonIcon, GameSettingsSelectorComponent],
  templateUrl: './init-game.html',
  styleUrl: './init-game.scss'
})
export class InitGameComponent {
  request: GameRequest = {
    playersIds: [],
    type: GameType.TYPE_501,
    finishRule: GameFinishRule.DOUBLE_OUT
  };

  constructor(private router: Router, private gameService: GameService) {}

  updateSettings(settings: {type: GameType, rule: string}) {
    this.request.type = settings.type;
    this.request.finishRule = settings.rule;
  }

  updatePlayers(ids: number[]) {
    this.request.playersIds = ids;
  }


  startGame() {
    if (this.request.playersIds.length === 0) {
      alert('Dodaaj przynajmniej jednego zawodnika, mordo.');
      return;
    }

    this.gameService.create(this.request).subscribe({
      next: (game) => this.router.navigate(['x01-game', game.uuid]),
      error: (err) => console.error('Błąd startu:', err)
    });
  }
}
