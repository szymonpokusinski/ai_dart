export interface IPlayer{
  id?: number;
  name: string;
}

export class Player implements IPlayer {
  id?: number;
  name: string;

  constructor(data?: IPlayer) {
    this.id = data?.id;
    this.name = data?.name || '';
  }
}
