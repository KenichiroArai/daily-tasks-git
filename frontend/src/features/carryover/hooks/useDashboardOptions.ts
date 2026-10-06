'use client';

import { useCallback, useMemo, useReducer } from 'react';

import { presetPeriod, type Period } from '../lib';
import type { DashboardOptions, Summary } from '../model';
import { createInitialState, dashboardReducer } from './dashboardReducer';

/**
 * 画面の表示条件と選択中の項目を管理する。
 */
export function useDashboardOptions(summary: Summary, initialOptions: Partial<DashboardOptions> = {}) {
  const [state, dispatch] = useReducer(dashboardReducer, undefined, () => createInitialState(summary, initialOptions));

  const setOptions = useCallback((patch: Partial<DashboardOptions>) => dispatch({ type: 'setOptions', patch }), []);
  const setPeriod = useCallback((period: Period) => dispatch({ type: 'setPeriod', period }), []);
  const applyPreset = useCallback(
    (days: number) => dispatch({ type: 'setPeriod', period: presetPeriod(summary.days, days) }),
    [summary.days],
  );
  const toggleItem = useCallback((item: string) => dispatch({ type: 'toggleItem', item }), []);
  const selectAll = useCallback(() => dispatch({ type: 'selectAll', items: summary.items }), [summary.items]);
  const selectNone = useCallback(() => dispatch({ type: 'selectNone' }), []);

  return useMemo(
    () => ({
      options: state.options,
      selected: state.selected,
      setOptions,
      setPeriod,
      applyPreset,
      toggleItem,
      selectAll,
      selectNone,
    }),
    [state, setOptions, setPeriod, applyPreset, toggleItem, selectAll, selectNone],
  );
}
