import {GameType} from './GameType';
import {GameStatus} from './GameStatus';
import {GameFinishRule} from './GameFinishRule';

export interface IGame {
  id: number | null;
  uuid: string | null;
  gameType: GameType | null;
  status: GameStatus | null;
  startTime: string | null;
  endTime: string | null;
  finishRule: GameFinishRule | null;
}

export class Game implements IGame {
  id: number | null;
  uuid: string | null;
  gameType: GameType | null;
  status: GameStatus | null;
  startTime: string | null;
  endTime: string | null;
  finishRule: GameFinishRule | null;

  constructor(data?: Partial<IGame>) {
    this.id = data?.id ?? null;
    this.uuid = data?.uuid ?? null;
    this.gameType = data?.gameType ?? null;
    this.status = data?.status ?? null;
    this.startTime = data?.startTime ?? null;
    this.endTime = data?.endTime ?? null;
    this.finishRule = data?.finishRule ?? null;
  }
}
