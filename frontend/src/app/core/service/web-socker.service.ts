import {RxStomp, RxStompConfig} from '@stomp/rx-stomp';
import {Injectable} from '@angular/core';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebSockerService extends RxStomp{
  constructor() {
    super();
  }

  public connect(): void {
    const stompConfig: RxStompConfig = {
      webSocketFactory: () => {
        return new SockJS('http://localhost:8080/ws-aidart')
      },
      connectHeaders:{

      },
      debug: (msg: string) => {
        console.log(new Date(), msg);
      },
      reconnectDelay: 5000,
    }
    this.configure(stompConfig);
    this.activate();
  }
}
