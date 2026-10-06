import { formatNumber, roundTo } from '@/shared/lib';

import { PALETTE, UNIT_LABELS } from '../constants';
import type { Agg, Day, Metric, Stat, Target } from '../model';

/**
 * 集計値から、指標と対象に応じた値を取り出す。
 */
export function statValue(stat: Stat | undefined, metric: Metric, target: Target): number {
  if (!stat) {
    return 0;
  }
  const useMinutes = metric !== 'count';
  const total = useMinutes ? stat.minutes : stat.count;
  const checked = useMinutes ? stat.checkedMinutes : stat.checkedCount;
  let value = total;
  if (target === 'checked') {
    value = checked;
  } else if (target === 'unchecked') {
    value = total - checked;
  }
  return metric === 'hours' ? value / 60 : value;
}

/**
 * ある日の、ある項目の値
 */
export function dayItemValue(day: Day, item: string, metric: Metric, target: Target): number {
  return statValue(day.byItem[item], metric, target);
}

/**
 * ある日の、選択した項目の合計値
 */
export function dayTotal(day: Day, items: Iterable<string>, metric: Metric, target: Target): number {
  let sum = 0;
  for (const item of items) {
    sum += dayItemValue(day, item, metric, target);
  }
  return sum;
}

/**
 * 値の一覧を集計する。空の場合は 0 を返す。
 */
export function aggregate(values: readonly number[], agg: Agg): number {
  if (values.length === 0) {
    return 0;
  }
  switch (agg) {
    case 'sum':
      return values.reduce((a, b) => a + b, 0);
    case 'max':
      return Math.max(...values);
    case 'min':
      return Math.min(...values);
    case 'last':
      return values[values.length - 1] ?? 0;
    default:
      return values.reduce((a, b) => a + b, 0) / values.length;
  }
}

/**
 * グラフや表に出す値の丸め（時間は小数 2 桁、それ以外は 1 桁）
 */
export function roundMetric(value: number, metric: Metric): number {
  return roundTo(value, metric === 'hours' ? 2 : 1);
}

/**
 * 指標に応じた表示用の書式化（分は整数、それ以外は小数 1 桁）
 */
export function formatMetric(value: number, metric: Metric): string {
  return formatNumber(value, metric === 'minutes' ? 0 : 1);
}

/**
 * 丸めと書式化をまとめて行い、単位を付ける。
 */
export function formatMetricWithUnit(value: number, metric: Metric): string {
  return `${formatMetric(roundMetric(value, metric), metric)} ${UNIT_LABELS[metric]}`;
}

/**
 * 日別のデータを月（YYYY-MM）ごとにまとめる。
 */
export function groupByMonth(days: readonly Day[]): Map<string, Day[]> {
  const months = new Map<string, Day[]>();
  for (const day of days) {
    const key = day.date.slice(0, 7);
    const list = months.get(key);
    if (list) {
      list.push(day);
    } else {
      months.set(key, [day]);
    }
  }
  return months;
}

/**
 * 期間（YYYY-MM-DD、両端を含む）で絞り込む。空文字は制限なしとする。
 */
export function filterDays(days: readonly Day[], from: string, to: string): Day[] {
  return days.filter((day) => (!from || day.date >= from) && (!to || day.date <= to));
}

/**
 * 項目の並び順に色を割り当てる。
 */
export function assignColors(items: readonly string[]): Record<string, string> {
  const result: Record<string, string> = {};
  items.forEach((item, index) => {
    result[item] = PALETTE[index % PALETTE.length] ?? PALETTE[0];
  });
  return result;
}

/**
 * 項目の元の並び順を保ったまま、選択した項目だけを返す。
 */
export function orderSelectedItems(items: readonly string[], selected: ReadonlySet<string>): string[] {
  return items.filter((item) => selected.has(item));
}
