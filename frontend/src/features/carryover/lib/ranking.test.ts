import { day, sampleSummary, stat } from '../testing/fixtures';
import { buildRanking } from './ranking';

describe('buildRanking', () => {
  const { days, items } = sampleSummary();

  it('最新の値の大きい順に並べ、最新・平均・最大・最小・合計を返す', () => {
    expect(buildRanking(days, items, 'count', 'all')).toEqual([
      { item: '英語', latest: 3, avg: 2, max: 3, min: 1, sum: 6 },
      { item: '数学', latest: 1, avg: 1, max: 2, min: 0, sum: 3 },
    ]);
  });

  it('最新の値が同じ場合は平均の大きい順に並べる', () => {
    const tieDays = [
      day('2026-01-01', 1, { 英語: stat(1, 15), 数学: stat(3, 45) }),
      day('2026-01-02', 2, { 英語: stat(2, 30), 数学: stat(2, 30) }),
    ];
    const rows = buildRanking(tieDays, items, 'count', 'all');
    expect(rows.map((row) => row.item)).toEqual(['数学', '英語']);
  });

  it('期間にデータがない場合はすべて 0', () => {
    expect(buildRanking([], ['英語'], 'count', 'all')).toEqual([
      { item: '英語', latest: 0, avg: 0, max: 0, min: 0, sum: 0 },
    ]);
  });
});
