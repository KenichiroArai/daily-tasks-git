import { sampleSummary } from '../testing/fixtures';
import { presetPeriod } from './period';

describe('presetPeriod', () => {
  const days = sampleSummary().days;

  it('データがない場合は空の期間を返す', () => {
    expect(presetPeriod([], 30)).toEqual({ from: '', to: '' });
  });

  it('0 は全期間', () => {
    expect(presetPeriod(days, 0)).toEqual({ from: '2026-01-30', to: '2026-02-01' });
  });

  it('直近 N 日は最新日を含めて N 日間（月をまたぐ）', () => {
    expect(presetPeriod(days, 2)).toEqual({ from: '2026-01-31', to: '2026-02-01' });
  });
});
