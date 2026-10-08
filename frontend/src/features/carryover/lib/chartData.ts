import { AGG_LABELS, METRIC_LABELS, ORIGIN_MONTH_COLOR, PRIMARY_COLOR } from '../constants';
import type { DashboardOptions, Day } from '../model';
import { aggregate, dayItemValue, dayTotal, groupByMonth, roundMetric, statValue } from './calc';

/**
 * グラフの系列。Recharts の dataKey は「.」をパスとして扱うため、項目名ではなく連番のキーを使う。
 */
export type ChartSeries = {
  key: string;
  label: string;
  color: string;
};

/**
 * グラフの 1 行（横軸の 1 目盛り）。label が横軸の値、系列のキーごとに数値を持つ。
 */
export type ChartRow = {
  label: string;
  color?: string;
  [seriesKey: string]: string | number | undefined;
};

/**
 * 描画に必要な情報をまとめたグラフの定義
 */
export type ChartSpec = {
  kind: 'area' | 'bar';
  stacked: boolean;
  horizontal: boolean;
  rows: ChartRow[];
  series: ChartSeries[];
};

type BuildContext = {
  days: readonly Day[];
  items: readonly string[];
  colors: Readonly<Record<string, string>>;
  options: DashboardOptions;
};

function seriesKey(index: number): string {
  const result = `s${index}`;
  return result;
}

function aggregatedLabel(options: DashboardOptions): string {
  const result = `${METRIC_LABELS[options.metric]}（${AGG_LABELS[options.agg]}）`;
  return result;
}

function buildDaily({ days, items, options }: BuildContext): ChartSpec {
  const { metric, target } = options;
  const result: ChartSpec = {
    kind: 'area',
    stacked: false,
    horizontal: false,
    rows: days.map((day) => ({ label: day.date, s0: roundMetric(dayTotal(day, items, metric, target), metric) })),
    series: [{ key: seriesKey(0), label: METRIC_LABELS[metric], color: PRIMARY_COLOR }],
  };
  return result;
}

function buildDailyItem({ days, items, colors, options }: BuildContext): ChartSpec {
  const { metric, target } = options;
  const result: ChartSpec = {
    kind: 'area',
    stacked: true,
    horizontal: false,
    rows: days.map((day) => {
      const result: ChartRow = { label: day.date };
      items.forEach((item, index) => {
        result[seriesKey(index)] = roundMetric(dayItemValue(day, item, metric, target), metric);
      });
      return result;
    }),
    series: items.map((item, index) => ({ key: seriesKey(index), label: item, color: colors[item] ?? PRIMARY_COLOR })),
  };
  return result;
}

function buildMonthly({ days, items, options }: BuildContext): ChartSpec {
  const { metric, target, agg } = options;
  const months = groupByMonth(days);
  const result: ChartSpec = {
    kind: 'bar',
    stacked: false,
    horizontal: false,
    rows: [...months].map(([month, monthDays]) => ({
      label: month,
      s0: roundMetric(
        aggregate(
          monthDays.map((day) => dayTotal(day, items, metric, target)),
          agg,
        ),
        metric,
      ),
    })),
    series: [{ key: seriesKey(0), label: aggregatedLabel(options), color: PRIMARY_COLOR }],
  };
  return result;
}

function buildMonthlyItem({ days, items, colors, options }: BuildContext): ChartSpec {
  const { metric, target, agg } = options;
  const months = groupByMonth(days);
  const result: ChartSpec = {
    kind: 'bar',
    stacked: true,
    horizontal: false,
    rows: [...months].map(([month, monthDays]) => {
      const result: ChartRow = { label: month };
      items.forEach((item, index) => {
        result[seriesKey(index)] = roundMetric(
          aggregate(
            monthDays.map((day) => dayItemValue(day, item, metric, target)),
            agg,
          ),
          metric,
        );
      });
      return result;
    }),
    series: items.map((item, index) => ({ key: seriesKey(index), label: item, color: colors[item] ?? PRIMARY_COLOR })),
  };
  return result;
}

function buildItem({ days, items, colors, options }: BuildContext): ChartSpec {
  const { metric, target, agg } = options;
  const rows = items
    .map((item) => ({
      item,
      value: aggregate(
        days.map((day) => dayItemValue(day, item, metric, target)),
        agg,
      ),
    }))
    .sort((a, b) => b.value - a.value);
  const result: ChartSpec = {
    kind: 'bar',
    stacked: false,
    horizontal: true,
    rows: rows.map((row) => ({ label: row.item, color: colors[row.item], s0: roundMetric(row.value, metric) })),
    series: [{ key: seriesKey(0), label: aggregatedLabel(options), color: PRIMARY_COLOR }],
  };
  return result;
}

function buildOriginMonth({ days, options }: BuildContext): ChartSpec {
  const { metric, target, agg } = options;
  const monthKeys = [...new Set(days.flatMap((day) => Object.keys(day.byOriginMonth)))].sort();
  const result: ChartSpec = {
    kind: 'bar',
    stacked: false,
    horizontal: false,
    rows: monthKeys.map((month) => ({
      label: month,
      s0: roundMetric(
        aggregate(
          days.map((day) => statValue(day.byOriginMonth[month], metric, target)),
          agg,
        ),
        metric,
      ),
    })),
    series: [{ key: seriesKey(0), label: aggregatedLabel(options), color: ORIGIN_MONTH_COLOR }],
  };
  return result;
}

const BUILDERS: Record<DashboardOptions['view'], (context: BuildContext) => ChartSpec> = {
  daily: buildDaily,
  dailyItem: buildDailyItem,
  monthly: buildMonthly,
  monthlyItem: buildMonthlyItem,
  item: buildItem,
  originMonth: buildOriginMonth,
};

/**
 * 表示の種類に応じたグラフの定義を作る。
 *
 * items は表示する（選択済みの）項目を元の並び順で渡す。持ち越し元の月ごとの表示は項目の選択に関係なく全項目を対象にする。
 */
export function buildChartSpec(
  days: readonly Day[],
  items: readonly string[],
  colors: Readonly<Record<string, string>>,
  options: DashboardOptions,
): ChartSpec {
  const result = BUILDERS[options.view]({ days, items, colors, options });
  return result;
}
