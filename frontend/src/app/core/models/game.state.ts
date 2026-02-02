import {IGamePlayer} from './game.player';

export interface IGameState {
  id: number;
  uuid: string;
  activePlayerId: number;
  finishRule: string;
  gameStatus: string;
  gameType: string;
  gamePlayers: IGamePlayer[];
}
