import type { ReactNode } from 'react';

import styles from './PageContainer.module.css';

type PageContainerProps = {
  children: ReactNode;
};

/**
 * ページ本文の幅と余白をそろえる入れ物
 */
export function PageContainer({ children }: PageContainerProps) {
  return <main className={styles.main}>{children}</main>;
}
