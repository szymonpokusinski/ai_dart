import { Component, EventEmitter, Inject, OnInit, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { IGamePlayer } from '../../core/models/game.player';
import { NgClass, NgIf, NgFor } from '@angular/common';

interface DialogData {
  players: IGamePlayer[];
}

@Component({
  selector: 'app-game-summary-dialog',
  templateUrl: './game-summary.dialog.component.html',
  styleUrls: ['./game-summary.dialog.component.scss'],
  standalone: true,
  imports: [
    NgClass,
    NgIf,
    NgFor
  ]
})
export class GameSummaryDialogComponent implements OnInit {
  public sortedPlayers: IGamePlayer[] = [];

  @Output() undoRequested = new EventEmitter<void>();
  @Output() saveRequested = new EventEmitter<void>();

  constructor(
    private dialogRef: MatDialogRef<GameSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  ngOnInit() {
    if (this.data?.players) {
      this.sortedPlayers = [...this.data.players].sort((a, b) => {
        const posA = a.finalPosition ?? 99;
        const posB = b.finalPosition ?? 99;
        return posA - posB;
      });
    }
  }

  onUndo(): void {
    this.undoRequested.emit();
    this.dialogRef.close();
  }

  onSave(): void {
    this.saveRequested.emit();
    this.dialogRef.close();
  }

  getPositionClass(position: number | null | undefined): string {
    if (!position) return 'text-white/20 border-white/5';

    switch (position) {
      case 1: return 'text-amber-500 border-amber-500/30 bg-amber-500/5 border';
      case 2: return 'text-slate-400 border-slate-400/30 bg-slate-400/5 border';
      case 3: return 'text-orange-600 border-orange-700/30 bg-orange-700/5 border';
      default: return 'text-white/40 border-white/10 bg-white/5 border';
    }
  }
}
