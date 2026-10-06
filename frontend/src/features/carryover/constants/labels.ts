import type { Agg, Metric, Target, View } from '../model';

export const VIEW_LABELS: Record<View, string> = {
  daily: '日別推移（全体）',
  dailyItem: '日別推移（項目ごと）',
  monthly: '月ごと（全体）',
  monthlyItem: '月ごと（項目ごと）',
  item: '項目ごと',
  originMonth: '持ち越し元の月ごと',
};

export const METRIC_LABELS: Record<Metric, string> = {
  count: '件数',
  minutes: '残り時間（分）',
  hours: '残り時間（時間）',
};

/**
 * セレクトボックスに表示する指標のラベル
 */
export const METRIC_OPTION_LABELS: Record<Metric, string> = {
  count: '件数（残）',
  minutes: '残り時間（分）',
  hours: '残り時間（時間）',
};

export const AGG_LABELS: Record<Agg, string> = {
  avg: '平均',
  sum: '合計',
  max: '最大',
  min: '最小',
  last: '期間末の値',
};

export const TARGET_LABELS: Record<Target, string> = {
  all: 'すべて',
  unchecked: '未チェックのみ',
  checked: 'チェック済みのみ',
};

export const UNIT_LABELS: Record<Metric, string> = {
  count: '件',
  minutes: '分',
  hours: '時間',
};

/**
 * 期間のプリセット（0 は全期間）
 */
export const PERIOD_PRESETS = [
  { days: 0, label: '全期間' },
  { days: 30, label: '直近30日' },
  { days: 90, label: '直近90日' },
] as const;
