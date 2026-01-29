import {GameType} from '../db/game/GameType';

export interface GameRequest {
  playersIds: number[];
  type: GameType;
  finishRule: string;
}
