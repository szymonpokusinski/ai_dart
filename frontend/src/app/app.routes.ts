import { Routes } from '@angular/router';
import { Home } from './home/home';
import {X01GameComponent} from './x01-game/x01-game';

export const routes: Routes = [
  {
    path: '',
    component: Home,
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: Home
  },
  {
    path: 'x01-game',
    component: X01GameComponent
  },
];
