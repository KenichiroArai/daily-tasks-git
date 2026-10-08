import { Swatch } from '@/shared/components/ui';

import { formatMetric, formatMetricWithUnit, roundMetric } from '../../lib';
import type { Metric } from '../../model';
import styles from './CarryoverChart.module.css';

/**
 * ツールチップに表示する 1 系列分の値
 */
export type ChartTooltipEntry = {
  name: string;
  value: number;
  color: string;
};

type ChartTooltipProps = {
  active?: boolean;
  label?: string | number;
  entries: readonly ChartTooltipEntry[];
  metric: Metric;
  stacked: boolean;
};

/**
 * 値の大きい順に系列を並べるツールチップ。積み上げ表示で 2 系列以上ある場合は合計も表示する。
 */
export function ChartTooltip({ active, label, entries, metric, stacked }: ChartTooltipProps) {
  if (!active || entries.length === 0) {
    return null;
  }
  const sorted = [...entries]
    .sort((a, b) => b.value - a.value)
    .map((entry) => ({ ...entry, text: formatMetric(entry.value, metric) }));
  const showTotal = stacked && entries.length >= 2;
  const total = entries.reduce((acc, entry) => acc + entry.value, 0);
  const totalText = formatMetricWithUnit(roundMetric(total, metric), metric);

  return (
    <div className={styles.tooltip}>
      <div className={styles.tooltipLabel}>{label}</div>
      <ul className={styles.tooltipList}>
        {sorted.map((entry) => (
          <li key={entry.name}>
            <Swatch color={entry.color} /> {entry.name}: {entry.text}
          </li>
        ))}
      </ul>
      {showTotal ? <div className={styles.tooltipTotal}>合計: {totalText}</div> : null}
    </div>
  );
}
