import { render, screen } from '@testing-library/react';

import { ChartTooltip } from './ChartTooltip';

const entries = [
  { name: '英語', value: 1, color: '#000' },
  { name: '数学', value: 3, color: '#111' },
];

describe('ChartTooltip', () => {
  it('非アクティブの場合は何も表示しない', () => {
    const { container } = render(<ChartTooltip entries={entries} metric="count" stacked />);
    expect(container).toBeEmptyDOMElement();
  });

  it('値の大きい順に並べる', () => {
    render(<ChartTooltip active label="2026-02-01" entries={entries} metric="count" stacked={false} />);
    const items = screen.getAllByRole('listitem').map((li) => li.textContent?.trim());
    expect(items).toEqual(['数学: 3', '英語: 1']);
    expect(screen.queryByText(/合計/)).not.toBeInTheDocument();
  });

  it('積み上げ表示では合計を表示する', () => {
    render(<ChartTooltip active label="2026-02-01" entries={entries} metric="count" stacked />);
    expect(screen.getByText('合計: 4 件')).toBeInTheDocument();
  });
});
