import { render, screen } from '@testing-library/react';

import { SummaryCards } from './SummaryCards';

describe('SummaryCards', () => {
  it('カードのラベル・値・補足を表示する', () => {
    render(<SummaryCards cards={[{ label: '最新の残（件数）', value: '4 件', sub: '2026-02-01（#3）' }]} />);
    expect(screen.getByText('最新の残（件数）')).toBeInTheDocument();
    expect(screen.getByText('4 件')).toBeInTheDocument();
    expect(screen.getByText('2026-02-01（#3）')).toBeInTheDocument();
  });
});
