import { Fragment } from 'react';

import styles from './Footer.module.css';

export type FooterLink = {
  label: string;
  href: string;
};

type FooterProps = {
  prefix?: string;
  links: readonly FooterLink[];
};

/**
 * ページ下部のリンク一覧
 */
export function Footer({ prefix, links }: FooterProps) {
  return (
    <footer className={styles.footer}>
      {prefix ? `${prefix} ` : null}
      {links.map((link, index) => (
        <Fragment key={link.href}>
          {index > 0 ? ' ／ ' : null}
          <a href={link.href}>{link.label}</a>
        </Fragment>
      ))}
    </footer>
  );
}
