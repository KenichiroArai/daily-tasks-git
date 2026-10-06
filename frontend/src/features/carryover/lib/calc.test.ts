import { day, sampleSummary, stat } from '../testing/fixtures';
import {
  aggregate,
  assignColors,
  dayItemValue,
  dayTotal,
  filterDays,
  formatMetric,
  formatMetricWithUnit,
  groupByMonth,
  orderSelectedItems,
  roundMetric,
  statValue,
} from './calc';

describe('statValue', () => {
  const s = stat(4, 90, 1, 30);

  it('集計値がない場合は 0 を返す', () => {
    expect(statValue(undefined, 'count', 'all')).toBe(0);
  });

  it('件数のすべて', () => {
    expect(statValue(s, 'count', 'all')).toBe(4);
  });

  it('件数のチェック済みのみ', () => {
    expect(statValue(s, 'count', 'checked')).toBe(1);
  });

  it('件数の未チェックのみ', () => {
    expect(statValue(s, 'count', 'unchecked')).toBe(3);
  });

  it('分の未チェックのみ', () => {
    expect(statValue(s, 'minutes', 'unchecked')).toBe(60);
  });

  it('時間は分を 60 で割る', () => {
    expect(statValue(s, 'hours', 'all')).toBe(1.5);
  });
});

describe('dayItemValue / dayTotal', () => {
  const d = day('2026-01-01', 1, { 英語: stat(2, 30), 数学: stat(1, 45) });

  it('項目の値を返す', () => {
    expect(dayItemValue(d, '数学', 'minutes', 'all')).toBe(45);
  });

  it('その日に存在しない項目は 0 を返す', () => {
    expect(dayItemValue(d, '国語', 'count', 'all')).toBe(0);
  });

  it('選択した項目だけを合計する', () => {
    expect(dayTotal(d, ['英語'], 'count', 'all')).toBe(2);
  });

  it('すべての項目を合計する', () => {
    expect(dayTotal(d, new Set(['英語', '数学']), 'minutes', 'all')).toBe(75);
  });
});

describe('aggregate', () => {
  const values = [3, 1, 2];

  it('空の場合は 0 を返す', () => {
    expect(aggregate([], 'avg')).toBe(0);
  });

  it('平均', () => {
    expect(aggregate(values, 'avg')).toBe(2);
  });

  it('合計', () => {
    expect(aggregate(values, 'sum')).toBe(6);
  });

  it('最大', () => {
    expect(aggregate(values, 'max')).toBe(3);
  });

  it('最小', () => {
    expect(aggregate(values, 'min')).toBe(1);
  });

  it('期間末の値', () => {
    expect(aggregate(values, 'last')).toBe(2);
  });
});

describe('roundMetric / formatMetric', () => {
  it('時間は小数 2 桁で丸める', () => {
    expect(roundMetric(1.234, 'hours')).toBe(1.23);
  });

  it('件数は小数 1 桁で丸める', () => {
    expect(roundMetric(1.25, 'count')).toBe(1.3);
  });

  it('分は整数で表示する', () => {
    expect(formatMetric(12.6, 'minutes')).toBe('13');
  });

  it('件数は小数 1 桁で表示する', () => {
    expect(formatMetric(1234.56, 'count')).toBe('1,234.6');
  });

  it('単位を付けて表示する', () => {
    expect(formatMetricWithUnit(1.256, 'hours')).toBe('1.3 時間');
  });
});

describe('groupByMonth', () => {
  it('月ごとにまとめる', () => {
    const months = groupByMonth(sampleSummary().days);
    expect([...months.keys()]).toEqual(['2026-01', '2026-02']);
    expect(months.get('2026-01')).toHaveLength(2);
  });
});

describe('filterDays', () => {
  const days = sampleSummary().days;

  it('期間の両端を含めて絞り込む', () => {
    expect(filterDays(days, '2026-01-31', '2026-02-01').map((d) => d.issue)).toEqual([2, 3]);
  });

  it('空文字は制限なしとする', () => {
    expect(filterDays(days, '', '')).toHaveLength(3);
  });
});

describe('assignColors / orderSelectedItems', () => {
  it('並び順に色を割り当て、パレットを一周したら最初に戻る', () => {
    const items = Array.from({ length: 17 }, (_, i) => `項目${i}`);
    const colors = assignColors(items);
    expect(colors['項目16']).toBe(colors['項目0']);
  });

  it('元の並び順を保って選択した項目を返す', () => {
    expect(orderSelectedItems(['a', 'b', 'c'], new Set(['c', 'a']))).toEqual(['a', 'c']);
  });
});
