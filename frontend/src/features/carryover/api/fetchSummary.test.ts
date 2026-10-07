import { sampleSummary } from '../testing/fixtures';
import { fetchSummary } from './fetchSummary';

function mockFetch(body: unknown, init: { ok?: boolean; status?: number } = {}): typeof fetch {
  return vi.fn(async () => ({
    ok: init.ok ?? true,
    status: init.status ?? 200,
    json: async () => body,
  })) as unknown as typeof fetch;
}

describe('fetchSummary', () => {
  it('summary.json を取得して返す', async () => {
    const summary = sampleSummary();
    const fetcher = mockFetch(summary);
    await expect(fetchSummary(fetcher)).resolves.toEqual(summary);
    expect(fetcher).toHaveBeenCalledWith('/data/summary.json', { cache: 'no-cache', signal: undefined });
  });

  it('シグナルを fetch に渡す', async () => {
    const fetcher = mockFetch(sampleSummary());
    const controller = new AbortController();
    await fetchSummary(fetcher, controller.signal);
    expect(fetcher).toHaveBeenCalledWith('/data/summary.json', { cache: 'no-cache', signal: controller.signal });
  });

  it('HTTP エラーの場合は例外を投げる', async () => {
    await expect(fetchSummary(mockFetch(null, { ok: false, status: 404 }))).rejects.toThrow(
      'summary.json の取得に失敗しました（404）',
    );
  });

  it('形式が想定と異なる場合は例外を投げる', async () => {
    await expect(fetchSummary(mockFetch({ items: [] }))).rejects.toThrow('summary.json の形式が想定と異なります');
  });
});
