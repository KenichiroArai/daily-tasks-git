import type { Day } from '../model';

export type Period = {
  from: string;
  to: string;
};

/**
 * 最新日を終わりとする直近 N 日間の期間を返す。N が 0 の場合は全期間とする。
 */
export function presetPeriod(days: readonly Day[], presetDays: number): Period {
  let result: Period = { from: '', to: '' };
  const first = days[0];
  const last = days[days.length - 1];
  if (!first || !last) {
    return result;
  }
  if (presetDays === 0) {
    result = { from: first.date, to: last.date };
    return result;
  }
  const from = new Date(`${last.date}T00:00:00Z`);
  from.setUTCDate(from.getUTCDate() - (presetDays - 1));
  result = { from: from.toISOString().slice(0, 10), to: last.date };
  return result;
}
