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
  const prefixText = prefix ? `${prefix} ` : null;

  return (
    <footer className={styles.footer}>
      {prefixText}
      {links.map((link, index) => (
        <Fragment key={link.href}>
          {index > 0 ? ' ／ ' : null}
          <a href={link.href}>{link.label}</a>
        </Fragment>
      ))}
    </footer>
  );
}
