/**
 * 指定した小数の桁数で四捨五入する。
 */
export function roundTo(value: number, digits: number): number {
  const factor = 10 ** digits;
  return Math.round(value * factor) / factor;
}

/**
 * 日本語のロケールで数値を書式化する。整数以外は指定した桁数に丸める。
 */
export function formatNumber(value: number, digits: number): string {
  const rounded = Number.isInteger(value) ? value : Number(value.toFixed(digits));
  return rounded.toLocaleString('ja-JP');
}
