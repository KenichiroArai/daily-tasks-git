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
});
