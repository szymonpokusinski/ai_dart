import { Component, EventEmitter, Output, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { IonIcon } from '@ionic/angular/standalone';
import { MatDialog } from '@angular/material/dialog';
import { Player } from '../../core/models/db/player.model';
import { PlayerService } from '../../core/service/player.service';
import { PlayerDialogComponent } from '../player-dialog/player-dialog.component';
// IMPORTY DLA DRAG & DROP
import {
  CdkDragDrop,
  CdkDrag,
  CdkDropList,
  CdkDragHandle,
  moveItemInArray,
  CdkDragPlaceholder
} from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-init-game-player-list',
  standalone: true,
  // DODAJ MODUŁY DO IMPORTS
  imports: [CommonModule, IonIcon, CdkDropList, CdkDrag, CdkDragHandle, CdkDragPlaceholder],
  templateUrl: './init-game-player-list.component.html',
  styleUrl: './init-game-player-list.component.scss'
})
export class InitGamePlayerListComponent implements OnInit {
  @Output() playersChanged = new EventEmitter<number[]>();

  private readonly playerService = inject(PlayerService);
  private readonly dialog = inject(MatDialog);
  private readonly cdr = inject(ChangeDetectorRef);

  players: Player[] = [];
  selectedPlayerIds: number[] = [];

  ngOnInit(): void {
    this.getPlayerList();
  }

  getPlayerList() {
    this.playerService.getMany().subscribe({
      next: (players) => {
        this.players = [...players];
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error:', err)
    });
  }

  // LOGIKA PRZESUWANIA
  drop(event: CdkDragDrop<Player[]>) {
    // Przesuwa element w tablicy
    moveItemInArray(this.players, event.previousIndex, event.currentIndex);

    // Po przesunięciu emitujemy nową kolejność ID (tylko tych zaznaczonych)
    this.emitChanges();
  }

  toggleSelection(playerId: number | undefined) {
    if (playerId === undefined) return;
    const index = this.selectedPlayerIds.indexOf(playerId);
    if (index > -1) {
      this.selectedPlayerIds.splice(index, 1);
    } else {
      this.selectedPlayerIds.push(playerId);
    }
    this.emitChanges();
  }

  private emitChanges() {
    // Emitujemy ID w takiej kolejności, w jakiej user ułożył je na liście
    const orderedSelectedIds = this.players
      .filter(p => this.selectedPlayerIds.includes(p.id!))
      .map(p => p.id!);

    this.playersChanged.emit(orderedSelectedIds);
  }

  isSelected(playerId: number | undefined): boolean {
    return playerId !== undefined && this.selectedPlayerIds.includes(playerId);
  }

  getInitials(name: string): string {
    return name ? name.slice(0, 2).toUpperCase() : '??';
  }

  openAddPlayerDialog() {
    const dialogRef = this.dialog.open(PlayerDialogComponent, { width: '350px' });
    dialogRef.afterClosed().subscribe(didSave => {
      if (didSave) this.getPlayerList();
    });
  }
}
