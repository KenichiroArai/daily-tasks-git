/**
 * グラフの表示の種類
 */
export const VIEWS = ['daily', 'dailyItem', 'monthly', 'monthlyItem', 'item', 'originMonth'] as const;
export type View = (typeof VIEWS)[number];

/**
 * 指標（件数・残り時間）
 */
export const METRICS = ['count', 'minutes', 'hours'] as const;
export type Metric = (typeof METRICS)[number];

/**
 * 期間内の集計方法
 */
export const AGGS = ['avg', 'sum', 'max', 'min', 'last'] as const;
export type Agg = (typeof AGGS)[number];

/**
 * 対象（チェックの有無）
 */
export const TARGETS = ['all', 'unchecked', 'checked'] as const;
export type Target = (typeof TARGETS)[number];

/**
 * 集計方法を選べる表示の種類
 */
export const AGG_VIEWS: ReadonlySet<View> = new Set<View>(['monthly', 'monthlyItem', 'item', 'originMonth']);

/**
 * 画面の表示条件
 */
export type DashboardOptions = {
  view: View;
  metric: Metric;
  agg: Agg;
  target: Target;
  from: string;
  to: string;
};

export const DEFAULT_OPTIONS: DashboardOptions = {
  view: 'daily',
  metric: 'count',
  agg: 'avg',
  target: 'all',
  from: '',
  to: '',
};
