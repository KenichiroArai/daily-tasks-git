'use client';

import styles from './DateRange.module.css';

type DateRangeProps = {
  from: string;
  to: string;
  min?: string;
  max?: string;
  onChange: (range: { from: string; to: string }) => void;
};

/**
 * 開始日と終了日の入力欄
 */
export function DateRange({ from, to, min, max, onChange }: DateRangeProps) {
  return (
    <div className={styles.inputs}>
      <input
        type="date"
        aria-label="開始日"
        value={from}
        min={min}
        max={max}
        onChange={(event) => onChange({ from: event.target.value, to })}
      />
      <span>〜</span>
      <input
        type="date"
        aria-label="終了日"
        value={to}
        min={min}
        max={max}
        onChange={(event) => onChange({ from, to: event.target.value })}
      />
    </div>
  );
}
