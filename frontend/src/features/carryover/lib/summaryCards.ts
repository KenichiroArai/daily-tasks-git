import { METRIC_LABELS } from '../constants';
import type { Day, Metric, Target } from '../model';
import { aggregate, dayTotal, formatMetric, formatMetricWithUnit } from './calc';

export type SummaryCardData = {
  label: string;
  value: string;
  sub: string;
};

/**
 * 画面上部のサマリカードの内容を作る。
 */
export function buildSummaryCards(
  days: readonly Day[],
  items: readonly string[],
  metric: Metric,
  target: Target,
): SummaryCardData[] {
  const last = days[days.length - 1];
  if (!last) {
    return [{ label: 'データなし', value: '-', sub: '期間や項目の選択を見直してください' }];
  }
  const prev = days.length > 1 ? days[days.length - 2] : undefined;
  const lastCount = dayTotal(last, items, 'count', target);
  const lastHours = dayTotal(last, items, 'hours', target);
  const values = days.map((day) => dayTotal(day, items, metric, target));
  const maxValue = Math.max(...values);
  const minValue = Math.min(...values);
  const maxDay = days[values.indexOf(maxValue)] ?? last;
  const minDay = days[values.indexOf(minValue)] ?? last;
  const diff = prev ? lastCount - dayTotal(prev, items, 'count', target) : 0;
  const sign = diff > 0 ? '+' : '';
  const metricLabel = METRIC_LABELS[metric];

  return [
    {
      label: '最新の残（件数）',
      value: `${formatMetric(lastCount, 'count')} 件`,
      sub: `${last.date}（#${last.issue}）${prev ? `／前日比 ${sign}${formatMetric(diff, 'count')}` : ''}`,
    },
    {
      label: '最新の残り時間',
      value: formatMetricWithUnit(lastHours, 'hours'),
      sub: formatMetricWithUnit(lastHours * 60, 'minutes'),
    },
    {
      label: `期間平均（${metricLabel}）`,
      value: formatMetricWithUnit(aggregate(values, 'avg'), metric),
      sub: `${days.length} 日間`,
    },
    { label: `最大（${metricLabel}）`, value: formatMetricWithUnit(maxValue, metric), sub: maxDay.date },
    { label: `最小（${metricLabel}）`, value: formatMetricWithUnit(minValue, metric), sub: minDay.date },
  ];
}
