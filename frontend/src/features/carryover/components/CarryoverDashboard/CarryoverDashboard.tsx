'use client';

import { useMemo } from 'react';

import { Panel } from '@/shared/components/ui';

import { useCarryoverSummary, useDashboardOptions, useQueryOptions } from '../../hooks';
import {
  assignColors,
  buildChartNote,
  buildChartSpec,
  buildChartTitle,
  buildRanking,
  buildSummaryCards,
  filterDays,
  orderSelectedItems,
} from '../../lib';
import type { Summary } from '../../model';
import { CarryoverChart } from '../CarryoverChart';
import { DashboardControls } from '../DashboardControls';
import { ItemSelector } from '../ItemSelector';
import { RankingTable } from '../RankingTable';
import { SummaryCards } from '../SummaryCards';
import styles from './CarryoverDashboard.module.css';

type DashboardViewProps = {
  summary: Summary;
};

/**
 * 読み込み済みのデータで画面を組み立てる。
 */
export function DashboardView({ summary }: DashboardViewProps) {
  const queryOptions = useQueryOptions();
  const { options, selected, setOptions, setPeriod, applyPreset, toggleItem, selectAll, selectNone } =
    useDashboardOptions(summary, queryOptions);

  const colors = useMemo(() => assignColors(summary.items), [summary.items]);
  const items = useMemo(() => orderSelectedItems(summary.items, selected), [summary.items, selected]);
  const days = useMemo(
    () => filterDays(summary.days, options.from, options.to),
    [summary.days, options.from, options.to],
  );
  const cards = useMemo(
    () => buildSummaryCards(days, items, options.metric, options.target),
    [days, items, options.metric, options.target],
  );
  const spec = useMemo(() => buildChartSpec(days, items, colors, options), [days, items, colors, options]);
  const ranking = useMemo(
    () => buildRanking(days, items, options.metric, options.target),
    [days, items, options.metric, options.target],
  );

  const firstDay = summary.days[0];
  const lastDay = summary.days[summary.days.length - 1];
  const chartTitle = buildChartTitle(options);
  const chartNote = buildChartNote(options);

  return (
    <>
      {lastDay ? (
        <p className={styles.meta}>
          最新: {lastDay.date}（#{summary.latestIssue}）
        </p>
      ) : null}
      <SummaryCards cards={cards} />
      <DashboardControls
        options={options}
        minDate={firstDay?.date}
        maxDate={lastDay?.date}
        onOptionsChange={setOptions}
        onPeriodChange={setPeriod}
        onPreset={applyPreset}
      />
      <ItemSelector
        items={summary.items}
        colors={colors}
        selected={selected}
        onToggle={toggleItem}
        onSelectAll={selectAll}
        onSelectNone={selectNone}
      />
      <CarryoverChart title={chartTitle} note={chartNote} spec={spec} metric={options.metric} />
      <RankingTable rows={ranking} colors={colors} metric={options.metric} />
    </>
  );
}

/**
 * 持ち越しの推移の画面。summary.json を読み込んで表示する。
 */
export function CarryoverDashboard() {
  const state = useCarryoverSummary();

  if (state.status === 'loading') {
    return <Panel note="データを読み込んでいます…">{null}</Panel>;
  }
  if (state.status === 'error') {
    return (
      <Panel title="データを読み込めませんでした" note={state.error.message}>
        {null}
      </Panel>
    );
  }
  return <DashboardView summary={state.data} />;
}
