import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {IonIcon} from '@ionic/angular/standalone';
import {Player} from '../../core/models/db/player.model';
import {CommonModule} from '@angular/common';
import {PlayerService} from '../../core/service/player.service';
import {MatDialog} from '@angular/material/dialog';
import {PlayerDialogComponent} from '../player-dialog/player-dialog.component';

@Component({
  selector: 'app-init-game-player-list',
  standalone: true,
  imports: [IonIcon, CommonModule],
  templateUrl: './init-game-player-list.component.html',
  styleUrl: './init-game-player-list.component.scss',
})
export class InitGamePlayerListComponent implements OnInit{

  private readonly playerService = inject(PlayerService);
  private readonly dialog = inject(MatDialog);
  private readonly cdr = inject(ChangeDetectorRef);

  players: Player[] = [];

  getPlayerList(){
    this.playerService.getMany().subscribe({
      next: (players) => {
        this.players = [... players];
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error fetching player list:', error);
      }
    })
  }

  ngOnInit(): void {
      this.getPlayerList();
  }

  selectedPlayerIds: number[] = [];

  toggleSelection(playerId: number | undefined) {
    if (playerId === undefined) return;

    const index = this.selectedPlayerIds.indexOf(playerId);
    if (index > -1) {
      this.selectedPlayerIds.splice(index, 1);
    } else {
      this.selectedPlayerIds.push(playerId);
    }
  }

  isSelected(playerId: number | undefined): boolean {
    return playerId !== undefined && this.selectedPlayerIds.includes(playerId);
  }

  getInitials(name: string): string {
    return name.slice(0, 2).toUpperCase();
  }

  openAddPlayerDialog() {
    const dialogRef = this.dialog.open(PlayerDialogComponent, {
      width: '350px'
    });
    dialogRef.afterClosed().subscribe((didSave: boolean) => {
      if(didSave){
        this.getPlayerList();
      }
    });
  }
}
