import { assetPath } from '@/shared/lib';

import { summarySchema, type Summary } from '../model';

export const SUMMARY_PATH = '/data/summary.json';

/**
 * summary.json を取得し、形式を検証して返す。
 *
 * @param fetcher 取得に使う関数（テストで差し替える）
 * @param signal 取得を中断するためのシグナル
 * @throws 取得に失敗した場合、中断された場合、または形式が想定と異なる場合
 */
export async function fetchSummary(fetcher: typeof fetch = fetch, signal?: AbortSignal): Promise<Summary> {
  const response = await fetcher(assetPath(SUMMARY_PATH), { cache: 'no-cache', signal });
  if (!response.ok) {
    throw new Error(`summary.json の取得に失敗しました（${response.status}）`);
  }
  const json: unknown = await response.json();
  const parsed = summarySchema.safeParse(json);
  if (!parsed.success) {
    throw new Error(`summary.json の形式が想定と異なります: ${parsed.error.issues[0]?.message ?? '不明なエラー'}`);
  }
  const result = parsed.data;
  return result;
}
