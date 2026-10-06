/**
 * 非同期に読み込むデータの状態
 */
export type LoadState<T> = { status: 'loading' } | { status: 'success'; data: T } | { status: 'error'; error: Error };
