import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { Select } from './Select';

const options = [
  { value: 'a', label: 'A' },
  { value: 'b', label: 'B' },
] as const;

describe('Select', () => {
  it('ラベルと選択肢を表示する', () => {
    render(<Select label="表示" value="a" options={options} onChange={() => {}} />);
    expect(screen.getByLabelText('表示')).toHaveValue('a');
    expect(screen.getAllByRole('option')).toHaveLength(2);
  });

  it('選択を変えると onChange が呼ばれる', async () => {
    const onChange = vi.fn();
    render(<Select label="表示" value="a" options={options} onChange={onChange} />);
    await userEvent.selectOptions(screen.getByLabelText('表示'), 'b');
    expect(onChange).toHaveBeenCalledWith('b');
  });

  it('disabled を指定すると操作できない', () => {
    render(<Select label="表示" value="a" options={options} onChange={() => {}} disabled />);
    expect(screen.getByLabelText('表示')).toBeDisabled();
  });
});
