import {AppServiceClientService} from './api-client.service';
import {IPlayer, Player} from '../models/db/player.model';
import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PlayerService extends AppServiceClientService<Player>{
  constructor() {
    super('players', (data: IPlayer) => new Player(data));
  }
}
