'use client';

import { Button, Panel, Swatch } from '@/shared/components/ui';

import styles from './ItemSelector.module.css';

type ItemSelectorProps = {
  items: readonly string[];
  colors: Readonly<Record<string, string>>;
  selected: ReadonlySet<string>;
  onToggle: (item: string) => void;
  onSelectAll: () => void;
  onSelectNone: () => void;
};

/**
 * グラフと表の対象にする項目の選択
 */
export function ItemSelector({ items, colors, selected, onToggle, onSelectAll, onSelectNone }: ItemSelectorProps) {
  return (
    <Panel>
      <div className={styles.header}>
        <span>項目</span>
        <Button onClick={onSelectAll}>すべて選択</Button>
        <Button onClick={onSelectNone}>すべて解除</Button>
      </div>
      <div className={styles.list}>
        {items.map((item) => (
          <label key={item} className={styles.item}>
            <input type="checkbox" checked={selected.has(item)} onChange={() => onToggle(item)} />
            <Swatch color={colors[item] ?? 'transparent'} />
            {item}
          </label>
        ))}
      </div>
    </Panel>
  );
}
