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
  const result: DashboardState = {
    options: { ...DEFAULT_OPTIONS, ...presetPeriod(summary.days, 0), ...initialOptions },
    selected: new Set(summary.items),
  };
  return result;
}

export function dashboardReducer(state: DashboardState, action: DashboardAction): DashboardState {
  let result = state;
  switch (action.type) {
    case 'setOptions':
      result = { ...state, options: { ...state.options, ...action.patch } };
      break;
    case 'setPeriod':
      result = { ...state, options: { ...state.options, ...action.period } };
      break;
    case 'toggleItem': {
      const selected = new Set(state.selected);
      if (selected.has(action.item)) {
        selected.delete(action.item);
      } else {
        selected.add(action.item);
      }
      result = { ...state, selected };
      break;
    }
    case 'selectAll':
      result = { ...state, selected: new Set(action.items) };
      break;
    case 'selectNone':
      result = { ...state, selected: new Set() };
      break;
    default:
      break;
  }
  return result;
}
