import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { DEFAULT_OPTIONS } from '../../model';
import { DashboardControls } from './DashboardControls';

function setup(view: (typeof DEFAULT_OPTIONS)['view'] = 'daily') {
  const result = { onOptionsChange: vi.fn(), onPeriodChange: vi.fn(), onPreset: vi.fn() };
  render(<DashboardControls options={{ ...DEFAULT_OPTIONS, view }} {...result} />);
  return result;
}

describe('DashboardControls', () => {
  it('表示を変えると onOptionsChange が呼ばれる', async () => {
    const { onOptionsChange } = setup();
    await userEvent.selectOptions(screen.getByLabelText('表示'), 'monthly');
    expect(onOptionsChange).toHaveBeenCalledWith({ view: 'monthly' });
  });

  it('日別推移では集計を選べない', () => {
    setup('daily');
    expect(screen.getByLabelText('集計')).toBeDisabled();
  });

  it('月ごとでは集計を選べる', () => {
    setup('monthly');
    expect(screen.getByLabelText('集計')).toBeEnabled();
  });

  it('プリセットを押すと日数が渡される', async () => {
    const { onPreset } = setup();
    await userEvent.click(screen.getByRole('button', { name: '直近30日' }));
    expect(onPreset).toHaveBeenCalledWith(30);
  });
});
