'use client';

import { useId } from 'react';

import styles from './Select.module.css';

export type SelectOption<T extends string> = {
  value: T;
  label: string;
};

type SelectProps<T extends string> = {
  label: string;
  value: T;
  options: readonly SelectOption<T>[];
  onChange: (value: T) => void;
  disabled?: boolean;
};

/**
 * ラベル付きのセレクトボックス
 */
export function Select<T extends string>({ label, value, options, onChange, disabled = false }: SelectProps<T>) {
  const id = useId();
  return (
    <div className={styles.control}>
      <label htmlFor={id} className={styles.label}>
        {label}
      </label>
      <select id={id} value={value} disabled={disabled} onChange={(event) => onChange(event.target.value as T)}>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </div>
  );
}
