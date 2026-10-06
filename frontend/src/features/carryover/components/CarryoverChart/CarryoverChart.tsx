'use client';

import type { ReactNode } from 'react';
import {
  Area,
  AreaChart,
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
  type TooltipContentProps,
} from 'recharts';

import { Panel } from '@/shared/components/ui';

import { METRIC_LABELS } from '../../constants';
import type { ChartRow, ChartSpec } from '../../lib';
import type { Metric } from '../../model';
import styles from './CarryoverChart.module.css';
import { ChartTooltip, type ChartTooltipEntry } from './ChartTooltip';

type CarryoverChartProps = {
  title: string;
  note: string;
  spec: ChartSpec;
  metric: Metric;
};

function toEntries(spec: ChartSpec, props: TooltipContentProps): ChartTooltipEntry[] {
  return (props.payload ?? []).map((item) => {
    const row = item.payload as ChartRow | undefined;
    const series = spec.series.find((s) => s.key === item.dataKey);
    return {
      name: spec.horizontal ? String(row?.label ?? '') : (series?.label ?? String(item.name ?? '')),
      value: Number(item.value ?? 0),
      color: row?.color ?? series?.color ?? '#000',
    };
  });
}

/**
 * 持ち越しのグラフ（Recharts）
 */
export function CarryoverChart({ title, note, spec, metric }: CarryoverChartProps) {
  const axisTitle = METRIC_LABELS[metric];
  const showLegend = spec.series.length > 1;

  const tooltip = (
    <Tooltip
      isAnimationActive={false}
      content={(props: TooltipContentProps) => (
        <ChartTooltip
          active={props.active}
          label={spec.horizontal ? undefined : (props.label as string | number | undefined)}
          entries={toEntries(spec, props)}
          metric={metric}
          stacked={spec.stacked}
        />
      )}
    />
  );
  const legend = showLegend ? <Legend verticalAlign="bottom" itemSorter={null} /> : null;
  const valueAxisLabel = { value: axisTitle, angle: -90, position: 'insideLeft' as const, offset: 10 };

  let chart: ReactNode;
  if (spec.kind === 'area') {
    chart = (
      <AreaChart data={spec.rows} margin={{ top: 8, right: 16, bottom: 8, left: 8 }}>
        <CartesianGrid strokeDasharray="3 3" vertical={false} />
        <XAxis dataKey="label" minTickGap={24} />
        <YAxis label={valueAxisLabel} allowDecimals />
        {tooltip}
        {legend}
        {spec.series.map((series) => (
          <Area
            key={series.key}
            type="monotone"
            dataKey={series.key}
            name={series.label}
            stackId={spec.stacked ? 'stack' : undefined}
            stroke={series.color}
            strokeWidth={spec.stacked ? 1 : 2}
            fill={series.color}
            fillOpacity={spec.stacked ? 0.6 : 0.15}
            dot={false}
            isAnimationActive={false}
          />
        ))}
      </AreaChart>
    );
  } else if (spec.horizontal) {
    chart = (
      <BarChart data={spec.rows} layout="vertical" margin={{ top: 8, right: 16, bottom: 24, left: 8 }}>
        <CartesianGrid strokeDasharray="3 3" horizontal={false} />
        <XAxis type="number" label={{ value: axisTitle, position: 'insideBottom', offset: -12 }} />
        <YAxis type="category" dataKey="label" width={280} interval={0} />
        {tooltip}
        {spec.series.map((series) => (
          <Bar key={series.key} dataKey={series.key} name={series.label} isAnimationActive={false}>
            {spec.rows.map((row) => (
              <Cell key={row.label} fill={row.color ?? series.color} />
            ))}
          </Bar>
        ))}
      </BarChart>
    );
  } else {
    chart = (
      <BarChart data={spec.rows} margin={{ top: 8, right: 16, bottom: 8, left: 8 }}>
        <CartesianGrid strokeDasharray="3 3" vertical={false} />
        <XAxis dataKey="label" />
        <YAxis label={valueAxisLabel} allowDecimals />
        {tooltip}
        {legend}
        {spec.series.map((series) => (
          <Bar
            key={series.key}
            dataKey={series.key}
            name={series.label}
            stackId={spec.stacked ? 'stack' : undefined}
            fill={series.color}
            isAnimationActive={false}
          />
        ))}
      </BarChart>
    );
  }

  return (
    <Panel title={title} note={note || undefined}>
      <div className={styles.wrap}>
        <ResponsiveContainer width="100%" height="100%">
          {chart}
        </ResponsiveContainer>
      </div>
    </Panel>
  );
}
