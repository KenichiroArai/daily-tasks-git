import { render, screen, within } from '@testing-library/react';

import { RankingTable } from './RankingTable';

describe('RankingTable', () => {
  it('項目ごとの行を指標に応じた書式で表示する', () => {
    render(
      <RankingTable
        rows={[{ item: '英語', latest: 90, avg: 45.4, max: 90, min: 0, sum: 1234 }]}
        colors={{}}
        metric="minutes"
      />,
    );
    const cells = within(screen.getAllByRole('row')[1]!).getAllByRole('cell');
    expect(cells.map((cell) => cell.textContent?.trim())).toEqual(['英語', '90', '45', '90', '0', '1,234']);
  });
});
