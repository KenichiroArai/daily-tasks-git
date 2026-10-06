import { sampleSummary } from '../testing/fixtures';
import { createInitialState, dashboardReducer } from './dashboardReducer';

describe('createInitialState', () => {
  it('全期間・全項目を選択した状態にする', () => {
    const state = createInitialState(sampleSummary());
    expect(state.options.from).toBe('2026-01-30');
    expect(state.options.to).toBe('2026-02-01');
    expect([...state.selected]).toEqual(['英語', '数学']);
  });

  it('初期値で表示条件を上書きする', () => {
    const state = createInitialState(sampleSummary(), { view: 'monthly' });
    expect(state.options.view).toBe('monthly');
  });
});

describe('dashboardReducer', () => {
  const initial = createInitialState(sampleSummary());

  it('表示条件を変更する', () => {
    const state = dashboardReducer(initial, { type: 'setOptions', patch: { metric: 'hours' } });
    expect(state.options.metric).toBe('hours');
  });

  it('期間を変更する', () => {
    const state = dashboardReducer(initial, { type: 'setPeriod', period: { from: '2026-02-01', to: '2026-02-01' } });
    expect(state.options.from).toBe('2026-02-01');
  });

  it('選択中の項目を外す', () => {
    const state = dashboardReducer(initial, { type: 'toggleItem', item: '英語' });
    expect(state.selected.has('英語')).toBe(false);
  });

  it('選択していない項目を選ぶ', () => {
    const none = dashboardReducer(initial, { type: 'selectNone' });
    const state = dashboardReducer(none, { type: 'toggleItem', item: '数学' });
    expect([...state.selected]).toEqual(['数学']);
  });

  it('すべて選択する', () => {
    const none = dashboardReducer(initial, { type: 'selectNone' });
    const state = dashboardReducer(none, { type: 'selectAll', items: ['英語', '数学'] });
    expect(state.selected.size).toBe(2);
  });
});
