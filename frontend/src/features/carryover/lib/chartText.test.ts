import { DEFAULT_OPTIONS } from '../model';
import { buildChartNote, buildChartTitle } from './chartText';

describe('buildChartTitle', () => {
  it('集計方法を選べない表示では集計方法を出さない', () => {
    expect(buildChartTitle({ ...DEFAULT_OPTIONS, view: 'daily' })).toBe('日別推移（全体）：件数（すべて）');
  });

  it('集計方法を選べる表示では集計方法を出す', () => {
    expect(
      buildChartTitle({ ...DEFAULT_OPTIONS, view: 'monthly', metric: 'hours', agg: 'max', target: 'checked' }),
    ).toBe('月ごと（全体）：残り時間（時間）・最大（チェック済みのみ）');
  });
});

describe('buildChartNote', () => {
  it('日別推移では補足なし', () => {
    expect(buildChartNote({ ...DEFAULT_OPTIONS, view: 'daily' })).toBe('');
  });

  it('月ごとでは集計の補足', () => {
    expect(buildChartNote({ ...DEFAULT_OPTIONS, view: 'monthly' })).toContain('集計は日ごとの残');
  });

  it('持ち越し元の月ごとでは全項目が対象である旨の補足', () => {
    expect(buildChartNote({ ...DEFAULT_OPTIONS, view: 'originMonth' })).toBe(
      '持ち越し元の月ごとの表示は項目の選択に関係なく全項目を対象にします。',
    );
  });
});
