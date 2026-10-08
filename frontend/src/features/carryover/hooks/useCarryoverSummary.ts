'use client';

import { useAsync } from '@/shared/hooks';
import type { LoadState } from '@/shared/types';

import { fetchSummary } from '../api';
import type { Summary } from '../model';

const loadSummary = (signal: AbortSignal) => fetchSummary(fetch, signal);

/**
 * summary.json を読み込み、読み込みの状態を返す。
 */
export function useCarryoverSummary(): LoadState<Summary> {
  const result = useAsync(loadSummary);
  return result;
}
