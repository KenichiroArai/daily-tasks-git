import { DEFAULT_OPTIONS, type DashboardOptions } from '../model';
import { sampleSummary } from '../testing/fixtures';
import { assignColors } from './calc';
import { buildChartSpec } from './chartData';

const summary = sampleSummary();
const colors = assignColors(summary.items);

function build(options: Partial<DashboardOptions>, items: readonly string[] = summary.items) {
  const result = buildChartSpec(summary.days, items, colors, { ...DEFAULT_OPTIONS, ...options });
  return result;
}

describe('buildChartSpec', () => {
  it('日別推移（全体）は選択した項目の合計の面グラフ', () => {
    const spec = build({ view: 'daily' });
    expect(spec.kind).toBe('area');
    expect(spec.stacked).toBe(false);
    expect(spec.rows).toEqual([
      { label: '2026-01-30', s0: 2 },
      { label: '2026-01-31', s0: 3 },
      { label: '2026-02-01', s0: 4 },
    ]);
  });

  it('日別推移（項目ごと）は項目ごとの積み上げ', () => {
    const spec = build({ view: 'dailyItem', metric: 'minutes' });
    expect(spec.stacked).toBe(true);
    expect(spec.series.map((s) => s.label)).toEqual(['英語', '数学']);
    expect(spec.rows[1]).toEqual({ label: '2026-01-31', s0: 15, s1: 60 });
  });

  it('月ごと（全体）は集計方法で集計する', () => {
    const spec = build({ view: 'monthly', agg: 'sum' });
    expect(spec.kind).toBe('bar');
    expect(spec.rows).toEqual([
      { label: '2026-01', s0: 5 },
      { label: '2026-02', s0: 4 },
    ]);
    expect(spec.series[0]?.label).toBe('件数（合計）');
  });

  it('月ごと（項目ごと）は項目ごとに集計して積み上げる', () => {
    const spec = build({ view: 'monthlyItem', agg: 'max' });
    expect(spec.stacked).toBe(true);
    expect(spec.rows[0]).toEqual({ label: '2026-01', s0: 2, s1: 2 });
  });

  it('項目ごとは値の大きい順の横棒で、行ごとに色を持つ', () => {
    const spec = build({ view: 'item', agg: 'sum' });
    expect(spec.horizontal).toBe(true);
    expect(spec.rows).toEqual([
      { label: '英語', color: colors['英語'], s0: 6 },
      { label: '数学', color: colors['数学'], s0: 3 },
    ]);
  });

  it('持ち越し元の月ごとは項目の選択に関係なく集計する', () => {
    const spec = build({ view: 'originMonth', agg: 'last', target: 'checked' }, []);
    expect(spec.rows).toEqual([
      { label: '2026-01', s0: 0 },
      { label: '2026-02', s0: 1 },
    ]);
  });

  it('選択した項目だけを対象にする', () => {
    const spec = build({ view: 'daily' }, ['数学']);
    expect(spec.rows.map((row) => row.s0)).toEqual([0, 2, 1]);
  });
});
