'use client';

import { useEffect, useState } from 'react';

import type { LoadState } from '../types';

/**
 * 非同期処理をマウント時に 1 回実行し、読み込みの状態を返す。
 *
 * loader は参照が変わるたびに再実行されるため、呼び出し側で安定した参照を渡す。
 */
export function useAsync<T>(loader: () => Promise<T>): LoadState<T> {
  const [state, setState] = useState<LoadState<T>>({ status: 'loading' });

  useEffect(() => {
    let cancelled = false;
    loader().then(
      (data) => {
        if (!cancelled) {
          setState({ status: 'success', data });
        }
      },
      (error: unknown) => {
        if (!cancelled) {
          setState({ status: 'error', error: error instanceof Error ? error : new Error(String(error)) });
        }
      },
    );
    return () => {
      cancelled = true;
    };
  }, [loader]);

  return state;
}
