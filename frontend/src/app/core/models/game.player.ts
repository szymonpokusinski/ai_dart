import {IVisit} from './visit';

export interface IGamePlayer {
  id?: number;
  finalPosition: number | null;
  gameId: number;
  playerId: number;
  playerName: string;
  scoreLeft: number;
  startPosition: number;
  visits: IVisit[];
}
