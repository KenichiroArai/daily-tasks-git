import type { Metadata } from 'next';
import type { ReactNode } from 'react';

import { siteConfig } from '@/config/site';
import { SUMMARY_PATH } from '@/features/carryover';
import { Footer, Header, PageContainer } from '@/shared/components/layout';
import { assetPath } from '@/shared/lib';

import '@/styles/globals.css';

export const metadata: Metadata = {
  title: `${siteConfig.title} | ${siteConfig.name}`,
  description: siteConfig.description,
};

export default function RootLayout({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <html lang="ja">
      <body>
        <Header title={siteConfig.title} description={siteConfig.description} />
        <PageContainer>{children}</PageContainer>
        <Footer
          prefix="データ:"
          links={[
            { label: `${siteConfig.repositoryName} の Issue`, href: siteConfig.issuesUrl },
            { label: 'summary.json', href: assetPath(SUMMARY_PATH) },
          ]}
        />
      </body>
    </html>
  );
}
