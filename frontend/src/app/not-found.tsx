import Link from 'next/link';

import { Panel } from '@/shared/components/ui';

export default function NotFound() {
  return (
    <Panel title="ページが見つかりません">
      <Link href="/">トップへ戻る</Link>
    </Panel>
  );
}
