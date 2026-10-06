import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { sampleSummary } from '../../testing/fixtures';
import { CarryoverDashboard, DashboardView } from './CarryoverDashboard';

describe('DashboardView', () => {
  it('最新の情報とサマリカードを表示する', () => {
    render(<DashboardView summary={sampleSummary()} />);
    expect(screen.getByText('最新: 2026-02-01（#3）')).toBeInTheDocument();
    const cards = screen.getByRole('region', { name: 'サマリ' });
    expect(within(cards).getByText('最新の残（件数）').nextSibling).toHaveTextContent('4 件');
  });

  it('項目を外すと集計表から消える', async () => {
    render(<DashboardView summary={sampleSummary()} />);
    await userEvent.click(screen.getByLabelText('数学'));
    const table = screen.getByRole('table');
    expect(within(table).queryByText('数学')).not.toBeInTheDocument();
  });

  it('表示を変えるとグラフの見出しが変わる', async () => {
    render(<DashboardView summary={sampleSummary()} />);
    await userEvent.selectOptions(screen.getByLabelText('表示'), 'monthly');
    expect(screen.getByRole('heading', { name: '月ごと（全体）：件数・平均（すべて）' })).toBeInTheDocument();
  });

  it('URL クエリの表示条件を初期値にする', () => {
    window.history.replaceState(null, '', '/?view=item&metric=hours');
    render(<DashboardView summary={sampleSummary()} />);
    expect(screen.getByLabelText('表示')).toHaveValue('item');
    expect(screen.getByLabelText('指標')).toHaveValue('hours');
    window.history.replaceState(null, '', '/');
  });
});

describe('CarryoverDashboard', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('読み込みに失敗した場合はエラーを表示する', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(async () => ({ ok: false, status: 500, json: async () => null })),
    );
    render(<CarryoverDashboard />);
    expect(await screen.findByText('データを読み込めませんでした')).toBeInTheDocument();
    expect(screen.getByText('summary.json の取得に失敗しました（500）')).toBeInTheDocument();
  });

  it('読み込みに成功した場合は画面を表示する', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn(async () => ({ ok: true, status: 200, json: async () => sampleSummary() })),
    );
    render(<CarryoverDashboard />);
    expect(await screen.findByText('最新: 2026-02-01（#3）')).toBeInTheDocument();
  });
});
