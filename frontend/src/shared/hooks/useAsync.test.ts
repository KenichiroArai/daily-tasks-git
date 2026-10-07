import { renderHook, waitFor } from '@testing-library/react';

import { useAsync } from './useAsync';

describe('useAsync', () => {
  it('成功すると success とデータを返す', async () => {
    const loader = () => Promise.resolve(42);
    const { result } = renderHook(() => useAsync(loader));
    expect(result.current.status).toBe('loading');
    await waitFor(() => expect(result.current).toEqual({ status: 'success', data: 42 }));
  });

  it('失敗すると error を返す', async () => {
    const loader = () => Promise.reject(new Error('失敗'));
    const { result } = renderHook(() => useAsync(loader));
    await waitFor(() => expect(result.current.status).toBe('error'));
    expect(result.current.status === 'error' && result.current.error.message).toBe('失敗');
  });

  it('Error 以外で失敗した場合は Error に包む', async () => {
    const loader = () => Promise.reject('文字列');
    const { result } = renderHook(() => useAsync(loader));
    await waitFor(() => expect(result.current.status).toBe('error'));
    expect(result.current.status === 'error' && result.current.error.message).toBe('文字列');
  });

  it('アンマウントすると loader に渡したシグナルを中断する', () => {
    let received: AbortSignal | undefined;
    const loader = (signal: AbortSignal) => {
      received = signal;
      return new Promise<number>(() => {});
    };
    const { unmount } = renderHook(() => useAsync(loader));
    expect(received?.aborted).toBe(false);
    unmount();
    expect(received?.aborted).toBe(true);
  });

  it('中断後に届いた結果は状態に反映しない', async () => {
    let resolve: (value: number) => void = () => {};
    const loader = () =>
      new Promise<number>((r) => {
        resolve = r;
      });
    const { result, unmount } = renderHook(() => useAsync(loader));
    unmount();
    resolve(42);
    await Promise.resolve();
    expect(result.current.status).toBe('loading');
  });

  it('中断による失敗はエラーとして扱わない', async () => {
    const loader = (signal: AbortSignal) =>
      new Promise<number>((_, reject) => {
        signal.addEventListener('abort', () => reject(new DOMException('Aborted', 'AbortError')));
      });
    const { result, unmount } = renderHook(() => useAsync(loader));
    unmount();
    await Promise.resolve();
    expect(result.current.status).toBe('loading');
  });
});
