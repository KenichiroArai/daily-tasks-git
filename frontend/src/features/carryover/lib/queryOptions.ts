import { AGGS, METRICS, TARGETS, VIEWS, type DashboardOptions } from '../model';

type QueryOptions = Partial<Pick<DashboardOptions, 'view' | 'metric' | 'agg' | 'target'>>;

function pick<T extends string>(params: URLSearchParams, key: string, allowed: readonly T[]): T | undefined {
  const value = params.get(key);
  const result = allowed.find((candidate) => candidate === value);
  return result;
}

/**
 * URL のクエリ文字列から表示条件の初期値を読み取る。想定外の値は無視する。
 *
 * 例: `?view=monthly&metric=hours`
 */
export function parseQueryOptions(search: string): QueryOptions {
  const params = new URLSearchParams(search);
  const result: QueryOptions = {};
  const view = pick(params, 'view', VIEWS);
  const metric = pick(params, 'metric', METRICS);
  const agg = pick(params, 'agg', AGGS);
  const target = pick(params, 'target', TARGETS);
  if (view) {
    result.view = view;
  }
  if (metric) {
    result.metric = metric;
  }
  if (agg) {
    result.agg = agg;
  }
  if (target) {
    result.target = target;
  }
  return result;
}
