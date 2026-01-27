import { Component, OnInit } from '@angular/core';
import { InitGameComponent } from '../init-game/init-game';
import {
  IonIcon,
} from '@ionic/angular/standalone';

import { addIcons } from 'ionicons';
import { peopleOutline, timeOutline } from 'ionicons/icons';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    InitGameComponent,
    IonIcon,
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home implements OnInit {
  constructor() {
    addIcons({
      'people-outline': peopleOutline,
      'time-outline': timeOutline
    });
  }

  ngOnInit() {}
}
