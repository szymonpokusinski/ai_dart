import {Component, inject, OnInit} from '@angular/core';
import {
  MatDialogRef,
} from '@angular/material/dialog';
import {IPlayer, Player} from '../../core/models/db/player.model';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {PlayerService} from '../../core/service/player.service';

@Component({
  selector: 'app-player-dialog',
  templateUrl: './player-dialog.component.html',
  styleUrls: ['./player-dialog.component.scss'],
  imports: [
    MatFormField,
    MatInput,
    FormsModule,
  ]
})
export class PlayerDialogComponent  implements OnInit {

  constructor() {}

  private readonly dialogRef = inject(MatDialogRef<PlayerDialogComponent>);
  private readonly playerService = inject(PlayerService);

  player: IPlayer = new Player();

  onSave(){
    this.playerService.create(this.player).subscribe({
      next: (savedPlayer) => {
        this.dialogRef.close(true);
      },
      error: (error) => {
        console.error('Error creating player:', error);
        this.dialogRef.close(false);
      }
    })
  }

  ngOnInit() {}

  onCancel() {
    this.dialogRef.close(false);
  }
}
