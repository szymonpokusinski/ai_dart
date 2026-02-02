import {ScoreMultiplier} from './score.multiplier';

export interface IShot{
  id?: number;
  gamerPlayerId: number;
  baseScore: number;
  totalScore: number;
  multiplier: ScoreMultiplier;
}
