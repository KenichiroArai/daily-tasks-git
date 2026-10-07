'use client';

import { useEffect, useState } from 'react';

import type { LoadState } from '../types';

/**
 * 非同期処理をマウント時に 1 回実行し、読み込みの状態を返す。
 *
 * loader には AbortSignal を渡す。アンマウント時や loader の参照が変わったときに中断され、
 * 中断後の結果（中断による失敗を含む）は捨てる。
 * loader は参照が変わるたびに再実行されるため、呼び出し側で安定した参照を渡す。
 */
export function useAsync<T>(loader: (signal: AbortSignal) => Promise<T>): LoadState<T> {
  const [state, setState] = useState<LoadState<T>>({ status: 'loading' });

  useEffect(() => {
    const controller = new AbortController();
    const { signal } = controller;
    loader(signal).then(
      (data) => {
        if (!signal.aborted) {
          setState({ status: 'success', data });
        }
      },
      (error: unknown) => {
        if (!signal.aborted) {
          setState({ status: 'error', error: error instanceof Error ? error : new Error(String(error)) });
        }
      },
    );
    return () => {
      controller.abort();
    };
  }, [loader]);

  return state;
}
