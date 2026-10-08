/**
 * basePath を付けた静的ファイルの URL を返す。
 *
 * GitHub Pages のプロジェクトサイトでは `/<リポジトリ名>` 配下で配信されるため、
 * `fetch` などで public/ のファイルを参照するときに使う。
 */
export function assetPath(path: string, basePath: string = process.env.NEXT_PUBLIC_BASE_PATH ?? ''): string {
  const normalizedBase = basePath.replace(/\/+$/, '');
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  const result = `${normalizedBase}${normalizedPath}`;
  return result;
}
