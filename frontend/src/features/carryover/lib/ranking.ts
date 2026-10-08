import type { Day, Metric, Target } from '../model';
import { aggregate, dayItemValue } from './calc';

export type RankingRow = {
  item: string;
  latest: number;
  avg: number;
  max: number;
  min: number;
  sum: number;
};

/**
 * 項目ごとの集計表の行を作る。最新の値の大きい順（同じなら平均の大きい順）に並べる。
 */
export function buildRanking(
  days: readonly Day[],
  items: readonly string[],
  metric: Metric,
  target: Target,
): RankingRow[] {
  const result = items
    .map((item) => {
      const values = days.map((day) => dayItemValue(day, item, metric, target));
      const result: RankingRow = {
        item,
        latest: values[values.length - 1] ?? 0,
        avg: aggregate(values, 'avg'),
        max: aggregate(values, 'max'),
        min: aggregate(values, 'min'),
        sum: aggregate(values, 'sum'),
      };
      return result;
    })
    .sort((a, b) => b.latest - a.latest || b.avg - a.avg);
  return result;
}
