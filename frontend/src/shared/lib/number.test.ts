import { formatNumber, roundTo } from './number';

describe('roundTo', () => {
  it('指定した桁数で四捨五入する', () => {
    expect(roundTo(1.256, 2)).toBe(1.26);
  });

  it('桁数が 0 の場合は整数にする', () => {
    expect(roundTo(2.5, 0)).toBe(3);
  });
});

describe('formatNumber', () => {
  it('整数は桁区切りを付ける', () => {
    expect(formatNumber(12345, 1)).toBe('12,345');
  });

  it('小数は指定した桁数に丸める', () => {
    expect(formatNumber(1.26, 1)).toBe('1.3');
  });
});
