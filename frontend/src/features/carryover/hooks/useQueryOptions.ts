'use client';

import { useState } from 'react';

import { parseQueryOptions } from '../lib';

/**
 * URL のクエリ文字列から表示条件の初期値を読み取る（初回の描画時に 1 回だけ）。
 *
 * データの読み込み後にクライアントだけで描画されるコンポーネントから呼び出す。
 */
export function useQueryOptions() {
  const [options] = useState(() => (typeof window === 'undefined' ? {} : parseQueryOptions(window.location.search)));
  return options;
}
