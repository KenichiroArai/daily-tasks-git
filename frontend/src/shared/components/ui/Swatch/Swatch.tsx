import styles from './Swatch.module.css';

type SwatchProps = {
  color: string;
};

/**
 * 系列の色を示す小さな四角
 */
export function Swatch({ color }: SwatchProps) {
  return <span className={styles.swatch} style={{ background: color }} aria-hidden="true" />;
}
