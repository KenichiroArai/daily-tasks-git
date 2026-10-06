import { presetPeriod, type Period } from '../lib';
import { DEFAULT_OPTIONS, type DashboardOptions, type Summary } from '../model';

/**
 * 画面の状態（表示条件と選択中の項目）
 */
export type DashboardState = {
  options: DashboardOptions;
  selected: ReadonlySet<string>;
};

export type DashboardAction =
  | { type: 'setOptions'; patch: Partial<DashboardOptions> }
  | { type: 'setPeriod'; period: Period }
  | { type: 'toggleItem'; item: string }
  | { type: 'selectAll'; items: readonly string[] }
  | { type: 'selectNone' };

/**
 * 初期状態を作る。期間は全期間、項目はすべて選択する。
 */
export function createInitialState(summary: Summary, initialOptions: Partial<DashboardOptions> = {}): DashboardState {
  return {
    options: { ...DEFAULT_OPTIONS, ...presetPeriod(summary.days, 0), ...initialOptions },
    selected: new Set(summary.items),
  };
}

export function dashboardReducer(state: DashboardState, action: DashboardAction): DashboardState {
  switch (action.type) {
    case 'setOptions':
      return { ...state, options: { ...state.options, ...action.patch } };
    case 'setPeriod':
      return { ...state, options: { ...state.options, ...action.period } };
    case 'toggleItem': {
      const selected = new Set(state.selected);
      if (selected.has(action.item)) {
        selected.delete(action.item);
      } else {
        selected.add(action.item);
      }
      return { ...state, selected };
    }
    case 'selectAll':
      return { ...state, selected: new Set(action.items) };
    case 'selectNone':
      return { ...state, selected: new Set() };
    default:
      return state;
  }
}
