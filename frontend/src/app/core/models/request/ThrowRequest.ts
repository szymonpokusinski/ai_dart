import {ScoreMultiplier} from '../db/shot/score.multiplier';

export interface IThrowRequest {
  score: number;
  multiplier: ScoreMultiplier;
}
