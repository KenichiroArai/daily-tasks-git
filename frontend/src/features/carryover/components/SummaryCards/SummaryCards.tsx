import { Card } from '@/shared/components/ui';

import type { SummaryCardData } from '../../lib';
import styles from './SummaryCards.module.css';

type SummaryCardsProps = {
  cards: readonly SummaryCardData[];
};

/**
 * 最新の残や期間の平均などのサマリカード
 */
export function SummaryCards({ cards }: SummaryCardsProps) {
  return (
    <section className={styles.cards} aria-label="サマリ">
      {cards.map((card) => (
        <Card key={card.label} label={card.label} value={card.value} sub={card.sub} />
      ))}
    </section>
  );
}
