import {IShot} from './db/shot/shot.model';

export interface IVisit {
  id: number;
  shots: IShot[],
  totalScore: number
}
