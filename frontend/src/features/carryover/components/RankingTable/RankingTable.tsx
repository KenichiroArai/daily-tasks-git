import { Panel, Swatch } from '@/shared/components/ui';

import { formatMetric, roundMetric, type RankingRow } from '../../lib';
import type { Metric } from '../../model';
import styles from './RankingTable.module.css';

type RankingTableProps = {
  rows: readonly RankingRow[];
  colors: Readonly<Record<string, string>>;
  metric: Metric;
};

const COLUMNS = [
  { key: 'latest', label: '最新' },
  { key: 'avg', label: '平均' },
  { key: 'max', label: '最大' },
  { key: 'min', label: '最小' },
  { key: 'sum', label: '合計' },
] as const;

/**
 * 項目ごとの集計表（選択した期間）
 */
export function RankingTable({ rows, colors, metric }: RankingTableProps) {
  const displayRows = rows.map((row) => ({
    item: row.item,
    cells: COLUMNS.map((column) => ({
      key: column.key,
      text: formatMetric(roundMetric(row[column.key], metric), metric),
    })),
  }));

  return (
    <Panel title="項目ごとの集計（選択した期間）">
      <div className={styles.wrap}>
        <table className={styles.table}>
          <thead>
            <tr>
              <th>項目</th>
              {COLUMNS.map((column) => (
                <th key={column.key}>{column.label}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {displayRows.map((row) => (
              <tr key={row.item}>
                <td>
                  <Swatch color={colors[row.item] ?? 'transparent'} /> {row.item}
                </td>
                {row.cells.map((cell) => (
                  <td key={cell.key}>{cell.text}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Panel>
  );
}
