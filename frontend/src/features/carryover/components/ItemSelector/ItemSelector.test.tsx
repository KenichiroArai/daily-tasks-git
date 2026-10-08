import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { ItemSelector } from './ItemSelector';

function setup() {
  const result = { onToggle: vi.fn(), onSelectAll: vi.fn(), onSelectNone: vi.fn() };
  render(<ItemSelector items={['英語', '数学']} colors={{}} selected={new Set(['英語'])} {...result} />);
  return result;
}

describe('ItemSelector', () => {
  it('選択状態をチェックボックスに反映する', () => {
    setup();
    expect(screen.getByLabelText('英語')).toBeChecked();
    expect(screen.getByLabelText('数学')).not.toBeChecked();
  });

  it('チェックボックスを押すと onToggle が呼ばれる', async () => {
    const { onToggle } = setup();
    await userEvent.click(screen.getByLabelText('数学'));
    expect(onToggle).toHaveBeenCalledWith('数学');
  });

  it('すべて選択・すべて解除のボタン', async () => {
    const { onSelectAll, onSelectNone } = setup();
    await userEvent.click(screen.getByRole('button', { name: 'すべて選択' }));
    await userEvent.click(screen.getByRole('button', { name: 'すべて解除' }));
    expect(onSelectAll).toHaveBeenCalledTimes(1);
    expect(onSelectNone).toHaveBeenCalledTimes(1);
  });
});
