import {CommonModule} from '@angular/common';
import {IonicModule} from '@ionic/angular';
import {ActivatedRoute, Router} from '@angular/router';
import {addIcons} from 'ionicons';
import {backspaceOutline, checkmarkCircle, closeOutline, refreshOutline, trophyOutline} from 'ionicons/icons';
import {ChangeDetectorRef, Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Subscription} from 'rxjs';
import {WebSockerService} from '../core/service/web-socker.service';
import {IGameState} from '../core/models/game.state';
import {IThrowRequest} from '../core/models/request/ThrowRequest';
import {ScoreMultiplier} from '../core/models/db/shot/score.multiplier';
import {MatDialog} from '@angular/material/dialog';
import {GameSummaryDialogComponent} from './game-summary.dialog/game-summary.dialog.component';

@Component({
  selector: 'app-x01-game',
  standalone: true,
  imports: [CommonModule, IonicModule],
  templateUrl: './x01-game.html',
  styleUrls: ['./x01-game.scss']
})
export class X01GameComponent implements OnInit, OnDestroy {

  gameUuid: string | null = null;
  currentGameState: IGameState | null = null;
  request: IThrowRequest = {score: 0, multiplier: ScoreMultiplier.SINGLE};

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef<HTMLElement>;

  private gameStateSubscription?: Subscription;
  private routeSubscription?: Subscription;
  private isSummaryOpen = false;

  constructor(
    private webSocketService: WebSockerService,
    private router: Router,
    private route: ActivatedRoute,
    private zone: NgZone,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog,
  ) {
    addIcons({backspaceOutline, checkmarkCircle, trophyOutline, refreshOutline, closeOutline});
  }

  ngOnDestroy(): void {
    this.gameStateSubscription?.unsubscribe();
    this.routeSubscription?.unsubscribe();
  }

  ngOnInit(): void {
    this.routeSubscription = this.route.paramMap.subscribe(params => {
      this.gameUuid = params.get('uuid');
      if (this.gameUuid) {
        this.initializeWebSocket();
      }
    });
  }

  private initializeWebSocket(): void {
    if (!this.gameUuid) return;

    this.webSocketService.connect();

    this.gameStateSubscription = this.webSocketService
      .watch(`/topic/game/${this.gameUuid}/state`)
      .subscribe((message) => {
        const state = JSON.parse(message.body) as IGameState;

        this.zone.run(() => {
          this.currentGameState = state;
          console.log(this.currentGameState);
          this.cdr.detectChanges();
          if (state.gameStatus === 'FINISHED') {
            this.openSummaryDialog();
          } else {
            this.isSummaryOpen = false;
          }
          if (state.gameStatus !== 'FINISHED') {
            this.scrollToActivePlayer();
          }
        });
      });

    this.joinGame();
  }

  joinGame(): void {
    if (!this.gameUuid) return;
    this.webSocketService.publish({
      destination: `/app/game/${this.gameUuid}/join`,
      body: ''
    });
  }

  setMultiplier(multiplier: ScoreMultiplier): void {
    this.request.multiplier = (this.request.multiplier === multiplier)
      ? ScoreMultiplier.SINGLE
      : multiplier;
  }

  handleThrowInput(score: number): void {
    if (!this.gameUuid) return;

    this.request.score = score;
    this.webSocketService.publish({
      destination: `/app/throw/${this.gameUuid}/throw`,
      body: JSON.stringify(this.request)
    });

    this.request.multiplier = ScoreMultiplier.SINGLE;
  }

  handleDeleteShot(): void {
    if (!this.gameUuid) return;
    this.webSocketService.publish({
      destination: `/app/throw/${this.gameUuid}/delete`,
      body: JSON.stringify(this.request)
    });
  }

  private openSummaryDialog(): void {
    if (this.isSummaryOpen) return;
    this.isSummaryOpen = true;

    const dialogRef = this.dialog.open(GameSummaryDialogComponent, {
      data: { players: this.currentGameState?.gamePlayers },
      panelClass: ['custom-glass-dialog', 'wider-dialog'],
      maxWidth: '400px',
      width: '80vw',
      disableClose: true
    });

    dialogRef.componentInstance.undoRequested.subscribe(() => {
      this.handleDeleteShot();
      this.isSummaryOpen = false;
    });

    dialogRef.componentInstance.saveRequested.subscribe(() => {
      this.confirmAndSaveGame();
    });
  }

  confirmAndSaveGame(): void {
    if (!this.gameUuid) return;

    this.webSocketService.publish({
      destination: `/app/game/${this.gameUuid}/finalize`,
      body: ''
    });
    this.router.navigate(['/home']);
  }

  private scrollToActivePlayer(): void {
    if (!this.currentGameState?.activePlayerId || !this.scrollContainer) return;

    setTimeout(() => {
      const activeId = this.currentGameState?.activePlayerId;
      const element = document.getElementById(`player-card-${activeId}`);
      const container = this.scrollContainer.nativeElement;

      if (element && container) {
        const elementRect = element.getBoundingClientRect();
        const containerRect = container.getBoundingClientRect();

        const currentScroll = container.scrollTop;

        const offsetTop = elementRect.top - containerRect.top;

        const centerOffset = (containerRect.height / 2) - (elementRect.height / 2);

        const targetScroll = currentScroll + offsetTop - centerOffset;

        container.scrollTo({
          top: targetScroll,
          behavior: 'smooth'
        });
      }
    }, 100);
  }

  readonly scoreButtons: number[] = [...Array.from({length: 20}, (_, i) => i + 1), 25];

  exitGame() {
    this.router.navigate(['/home']);
  }

  protected readonly ScoreMultiplier = ScoreMultiplier;
}
