import { parseQueryOptions } from './queryOptions';

describe('parseQueryOptions', () => {
  it('クエリがない場合は空', () => {
    expect(parseQueryOptions('')).toEqual({});
  });

  it('すべての表示条件を読み取る', () => {
    expect(parseQueryOptions('?view=monthly&metric=hours&agg=max&target=checked')).toEqual({
      view: 'monthly',
      metric: 'hours',
      agg: 'max',
      target: 'checked',
    });
  });

  it('想定外の値は無視する', () => {
    expect(parseQueryOptions('?view=unknown&metric=count')).toEqual({ metric: 'count' });
  });
});
