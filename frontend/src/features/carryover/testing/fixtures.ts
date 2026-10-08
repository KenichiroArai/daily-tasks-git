import type { Day, Stat, Summary } from '../model';

/**
 * テスト用の集計値を作る。
 */
export function stat(count: number, minutes: number, checkedCount = 0, checkedMinutes = 0): Stat {
  const result: Stat = { count, minutes, checkedCount, checkedMinutes };
  return result;
}

/**
 * テスト用の 1 日分の集計を作る。total は byItem の合計にする。
 */
export function day(
  date: string,
  issue: number,
  byItem: Record<string, Stat>,
  byOriginMonth: Record<string, Stat> = {},
): Day {
  const total = Object.values(byItem).reduce(
    (acc, s) =>
      stat(
        acc.count + s.count,
        acc.minutes + s.minutes,
        acc.checkedCount + s.checkedCount,
        acc.checkedMinutes + s.checkedMinutes,
      ),
    stat(0, 0),
  );
  const result: Day = { date, issue, declaredCount: null, total, byItem, byOriginMonth };
  return result;
}

/**
 * テスト用の summary.json（3 日分、2 項目）
 */
export function sampleSummary(): Summary {
  const result: Summary = {
    latestIssue: 3,
    items: ['英語', '数学'],
    days: [
      day('2026-01-30', 1, { 英語: stat(2, 30) }, { '2026-01': stat(2, 30) }),
      day('2026-01-31', 2, { 英語: stat(1, 15), 数学: stat(2, 60, 1, 30) }, { '2026-01': stat(3, 75, 1, 30) }),
      day(
        '2026-02-01',
        3,
        { 英語: stat(3, 45, 1, 15), 数学: stat(1, 30) },
        { '2026-01': stat(2, 30), '2026-02': stat(2, 45, 1, 15) },
      ),
    ],
  };
  return result;
}
