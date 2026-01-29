import {AppServiceClientService} from './api-client.service';
import {Game, IGame} from '../models/db/game/game.model';
import {Injectable} from '@angular/core';
import {map, Observable} from 'rxjs';
import {GameRequest} from '../models/request/game.request';

@Injectable({
  providedIn: 'root'
})
export class GameService extends AppServiceClientService<IGame>{
  constructor() {
    super('games', (data: IGame) => new Game(data));
  }

  override create(payload: any): Observable<IGame> {
    return this.http.post<IGame>(`${this.baseUrl}/${this.endpoint}`, payload).pipe(
      map(data => this.mapFn ? this.mapFn(data) : data)
    );
  }
}
