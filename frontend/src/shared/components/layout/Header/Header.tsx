import styles from './Header.module.css';

type HeaderProps = {
  title: string;
  description?: string;
};

/**
 * ページ上部の見出し
 */
export function Header({ title, description }: HeaderProps) {
  return (
    <header className={styles.header}>
      <h1 className={styles.title}>{title}</h1>
      {description ? <p className={styles.description}>{description}</p> : null}
    </header>
  );
}
