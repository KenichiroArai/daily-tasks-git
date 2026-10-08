import type { ReactNode } from 'react';

import styles from './Panel.module.css';

type PanelProps = {
  title?: ReactNode;
  note?: ReactNode;
  className?: string;
  children: ReactNode;
};

/**
 * 枠付きの区画。見出しと補足を任意で表示する。
 */
export function Panel({ title, note, className, children }: PanelProps) {
  const sectionClassName = [styles.panel, className].filter(Boolean).join(' ');

  return (
    <section className={sectionClassName}>
      {title ? <h2 className={styles.title}>{title}</h2> : null}
      {note ? <p className={styles.note}>{note}</p> : null}
      {children}
    </section>
  );
}
