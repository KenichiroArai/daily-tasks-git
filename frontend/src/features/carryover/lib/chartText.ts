import { AGG_LABELS, METRIC_LABELS, TARGET_LABELS, VIEW_LABELS } from '../constants';
import { AGG_VIEWS, type DashboardOptions } from '../model';

/**
 * グラフの見出し
 */
export function buildChartTitle(options: DashboardOptions): string {
  const aggText = AGG_VIEWS.has(options.view) ? `・${AGG_LABELS[options.agg]}` : '';
  const result = `${VIEW_LABELS[options.view]}：${METRIC_LABELS[options.metric]}${aggText}（${TARGET_LABELS[options.target]}）`;
  return result;
}

/**
 * グラフの補足
 */
export function buildChartNote(options: DashboardOptions): string {
  const notes: string[] = [];
  if (options.view === 'originMonth') {
    notes.push('持ち越し元の月ごとの表示は項目の選択に関係なく全項目を対象にします。');
  }
  if (AGG_VIEWS.has(options.view) && options.view !== 'originMonth') {
    notes.push('集計は日ごとの残をもとに計算します（例: 平均は期間内の 1 日あたりの残）。');
  }
  const result = notes.join(' ');
  return result;
}
