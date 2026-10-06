import { assetPath } from './assetPath';

describe('assetPath', () => {
  it('basePath がない場合はパスをそのまま返す', () => {
    expect(assetPath('data/summary.json', '')).toBe('/data/summary.json');
  });

  it('basePath を先頭に付ける', () => {
    expect(assetPath('/data/summary.json', '/daily-tasks-git')).toBe('/daily-tasks-git/data/summary.json');
  });

  it('basePath の末尾のスラッシュを取り除く', () => {
    expect(assetPath('data/summary.json', '/daily-tasks-git/')).toBe('/daily-tasks-git/data/summary.json');
  });
});
