import styles from './Card.module.css';

type CardProps = {
  label: string;
  value: string;
  sub?: string;
};

/**
 * ラベル・値・補足を縦に並べる小さなカード
 */
export function Card({ label, value, sub }: CardProps) {
  return (
    <div className={styles.card}>
      <div className={styles.label}>{label}</div>
      <div className={styles.value}>{value}</div>
      {sub ? <div className={styles.sub}>{sub}</div> : null}
    </div>
  );
}
