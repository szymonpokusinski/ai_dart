import { Component, Output, EventEmitter } from '@angular/core';
import {GameType} from '../../core/models/db/game/GameType';
import {GameFinishRule} from '../../core/models/db/game/GameFinishRule';

@Component({
  selector: 'app-game-settings',
  templateUrl: 'game-settings.html',
})
export class GameSettingsSelectorComponent {
  @Output() settingsChanged = new EventEmitter<{type: GameType, rule: GameFinishRule}>();

  isModalOpen = false;
  modalMode: 'type' | 'rule' = 'type';

  selectedType: GameType = GameType.TYPE_501;
  selectedRule: GameFinishRule = GameFinishRule.DOUBLE_OUT;

  typeOptions = Object.values(GameType);
  ruleOptions = Object.values(GameFinishRule);

  get currentOptions() {
    return this.modalMode === 'type' ? this.typeOptions : this.ruleOptions;
  }

  openModal(mode: 'type' | 'rule') {
    this.modalMode = mode;
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  isSelected(option: any): boolean {
    return this.modalMode === 'type' ? this.selectedType === option : this.selectedRule === option;
  }

  selectOption(option: any) {
    if (this.modalMode === 'type') {
      this.selectedType = option;
    } else {
      this.selectedRule = option;
    }
    this.settingsChanged.emit({ type: this.selectedType, rule: this.selectedRule });
    setTimeout(() => this.closeModal(), 150);
  }
}
