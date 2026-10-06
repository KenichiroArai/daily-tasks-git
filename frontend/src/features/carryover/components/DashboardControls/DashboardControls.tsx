'use client';

import { Button, DateRange, Panel, Select, type SelectOption } from '@/shared/components/ui';

import { AGG_LABELS, METRIC_OPTION_LABELS, PERIOD_PRESETS, TARGET_LABELS, VIEW_LABELS } from '../../constants';
import type { Period } from '../../lib';
import { AGGS, AGG_VIEWS, METRICS, TARGETS, VIEWS, type DashboardOptions } from '../../model';
import styles from './DashboardControls.module.css';

function toOptions<T extends string>(values: readonly T[], labels: Record<T, string>): SelectOption<T>[] {
  return values.map((value) => ({ value, label: labels[value] }));
}

const VIEW_OPTIONS = toOptions(VIEWS, VIEW_LABELS);
const METRIC_OPTIONS = toOptions(METRICS, METRIC_OPTION_LABELS);
const AGG_OPTIONS = toOptions(AGGS, AGG_LABELS);
const TARGET_OPTIONS = toOptions(TARGETS, TARGET_LABELS);

type DashboardControlsProps = {
  options: DashboardOptions;
  minDate?: string;
  maxDate?: string;
  onOptionsChange: (patch: Partial<DashboardOptions>) => void;
  onPeriodChange: (period: Period) => void;
  onPreset: (days: number) => void;
};

/**
 * 表示・指標・集計・対象・期間の切り替え
 */
export function DashboardControls({
  options,
  minDate,
  maxDate,
  onOptionsChange,
  onPeriodChange,
  onPreset,
}: DashboardControlsProps) {
  return (
    <Panel className={styles.controls}>
      <Select label="表示" value={options.view} options={VIEW_OPTIONS} onChange={(view) => onOptionsChange({ view })} />
      <Select
        label="指標"
        value={options.metric}
        options={METRIC_OPTIONS}
        onChange={(metric) => onOptionsChange({ metric })}
      />
      <Select
        label="集計"
        value={options.agg}
        options={AGG_OPTIONS}
        disabled={!AGG_VIEWS.has(options.view)}
        onChange={(agg) => onOptionsChange({ agg })}
      />
      <Select
        label="対象"
        value={options.target}
        options={TARGET_OPTIONS}
        onChange={(target) => onOptionsChange({ target })}
      />
      <div className={styles.period}>
        <span className={styles.label}>期間</span>
        <DateRange from={options.from} to={options.to} min={minDate} max={maxDate} onChange={onPeriodChange} />
        <div className={styles.presets}>
          {PERIOD_PRESETS.map((preset) => (
            <Button key={preset.days} onClick={() => onPreset(preset.days)}>
              {preset.label}
            </Button>
          ))}
        </div>
      </div>
    </Panel>
  );
}
