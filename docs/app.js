'use strict';

(() => {
  const PALETTE = [
    '#0969da', '#cf222e', '#1a7f37', '#9a6700', '#8250df', '#bf3989', '#0a3069', '#e16f24',
    '#2da44e', '#6e7781', '#a40e26', '#116329', '#953800', '#512a97', '#57606a', '#4ac26b',
  ];

  const VIEW_LABELS = {
    daily: '日別推移（全体）',
    dailyItem: '日別推移（項目ごと）',
    monthly: '月ごと（全体）',
    monthlyItem: '月ごと（項目ごと）',
    item: '項目ごと',
    originMonth: '持ち越し元の月ごと',
  };
  const METRIC_LABELS = { count: '件数', minutes: '残り時間（分）', hours: '残り時間（時間）' };
  const AGG_LABELS = { avg: '平均', sum: '合計', max: '最大', min: '最小', last: '期間末の値' };
  const TARGET_LABELS = { all: 'すべて', unchecked: '未チェックのみ', checked: 'チェック済みのみ' };
  const AGG_VIEWS = new Set(['monthly', 'monthlyItem', 'item', 'originMonth']);

  const $ = (id) => document.getElementById(id);

  const state = {
    summary: null,
    colors: {},
    selected: new Set(),
    chart: null,
  };

  /* ---------- 値の計算 ---------- */

  function statValue(stat, metric, target) {
    if (!stat) {
      return 0;
    }
    const useMinutes = metric !== 'count';
    const total = useMinutes ? stat.minutes : stat.count;
    const checked = useMinutes ? stat.checkedMinutes : stat.checkedCount;
    let value = total;
    if (target === 'checked') {
      value = checked;
    } else if (target === 'unchecked') {
      value = total - checked;
    }
    return metric === 'hours' ? value / 60 : value;
  }

  function dayItemValue(day, item, metric, target) {
    return statValue(day.byItem[item], metric, target);
  }

  function dayTotal(day, metric, target) {
    let sum = 0;
    for (const item of state.selected) {
      sum += dayItemValue(day, item, metric, target);
    }
    return sum;
  }

  function aggregate(values, agg) {
    if (values.length === 0) {
      return 0;
    }
    switch (agg) {
      case 'sum':
        return values.reduce((a, b) => a + b, 0);
      case 'max':
        return Math.max(...values);
      case 'min':
        return Math.min(...values);
      case 'last':
        return values[values.length - 1];
      default:
        return values.reduce((a, b) => a + b, 0) / values.length;
    }
  }

  function round(value, metric) {
    const digits = metric === 'hours' ? 2 : 1;
    const factor = 10 ** digits;
    return Math.round(value * factor) / factor;
  }

  function format(value, metric) {
    const digits = metric === 'count' ? 1 : (metric === 'hours' ? 1 : 0);
    const rounded = Number.isInteger(value) ? value : Number(value.toFixed(digits));
    return rounded.toLocaleString('ja-JP');
  }

  function unit(metric) {
    return { count: '件', minutes: '分', hours: '時間' }[metric];
  }

  function groupByMonth(days) {
    const months = new Map();
    for (const day of days) {
      const key = day.date.slice(0, 7);
      if (!months.has(key)) {
        months.set(key, []);
      }
      months.get(key).push(day);
    }
    return months;
  }

  function selectedItemsOrdered() {
    return state.summary.items.filter((item) => state.selected.has(item));
  }

  /* ---------- 条件 ---------- */

  function currentOptions() {
    return {
      view: $('view').value,
      metric: $('metric').value,
      agg: $('agg').value,
      target: $('target').value,
      from: $('from').value,
      to: $('to').value,
    };
  }

  function filteredDays(options) {
    return state.summary.days.filter((day) =>
      (!options.from || day.date >= options.from) && (!options.to || day.date <= options.to));
  }

  /* ---------- グラフのデータ ---------- */

  function buildChart(days, options) {
    const { view, metric, agg, target } = options;
    const items = selectedItemsOrdered();
    const r = (v) => round(v, metric);

    if (view === 'daily') {
      return {
        type: 'line',
        labels: days.map((d) => d.date),
        datasets: [{
          label: METRIC_LABELS[metric],
          data: days.map((d) => r(dayTotal(d, metric, target))),
          borderColor: PALETTE[0],
          backgroundColor: 'rgba(9, 105, 218, 0.15)',
          fill: true,
          pointRadius: 0,
          tension: 0.2,
        }],
        stacked: false,
      };
    }

    if (view === 'dailyItem') {
      return {
        type: 'line',
        labels: days.map((d) => d.date),
        datasets: items.map((item) => ({
          label: item,
          data: days.map((d) => r(dayItemValue(d, item, metric, target))),
          borderColor: state.colors[item],
          backgroundColor: `${state.colors[item]}99`,
          fill: true,
          pointRadius: 0,
          borderWidth: 1,
        })),
        stacked: true,
      };
    }

    if (view === 'monthly' || view === 'monthlyItem') {
      const months = groupByMonth(days);
      const labels = [...months.keys()];
      if (view === 'monthly') {
        return {
          type: 'bar',
          labels,
          datasets: [{
            label: `${METRIC_LABELS[metric]}（${AGG_LABELS[agg]}）`,
            data: labels.map((m) => r(aggregate(months.get(m).map((d) => dayTotal(d, metric, target)), agg))),
            backgroundColor: PALETTE[0],
          }],
          stacked: false,
        };
      }
      return {
        type: 'bar',
        labels,
        datasets: items.map((item) => ({
          label: item,
          data: labels.map((m) => r(aggregate(months.get(m).map((d) => dayItemValue(d, item, metric, target)), agg))),
          backgroundColor: state.colors[item],
        })),
        stacked: true,
      };
    }

    if (view === 'item') {
      const rows = items
        .map((item) => ({ item, value: aggregate(days.map((d) => dayItemValue(d, item, metric, target)), agg) }))
        .sort((a, b) => b.value - a.value);
      return {
        type: 'bar',
        labels: rows.map((row) => row.item),
        datasets: [{
          label: `${METRIC_LABELS[metric]}（${AGG_LABELS[agg]}）`,
          data: rows.map((row) => r(row.value)),
          backgroundColor: rows.map((row) => state.colors[row.item]),
        }],
        stacked: false,
        horizontal: true,
      };
    }

    // 持ち越し元の月ごと
    const monthKeys = [...new Set(days.flatMap((d) => Object.keys(d.byOriginMonth)))].sort();
    return {
      type: 'bar',
      labels: monthKeys,
      datasets: [{
        label: `${METRIC_LABELS[metric]}（${AGG_LABELS[agg]}）`,
        data: monthKeys.map((m) => r(aggregate(days.map((d) => statValue(d.byOriginMonth[m], metric, target)), agg))),
        backgroundColor: PALETTE[4],
      }],
      stacked: false,
    };
  }

  function renderChart(days, options) {
    const spec = buildChart(days, options);
    if (state.chart) {
      state.chart.destroy();
    }
    const axisTitle = `${METRIC_LABELS[options.metric]}`;
    const valueAxis = { stacked: spec.stacked, beginAtZero: true, title: { display: true, text: axisTitle } };
    const categoryAxis = { stacked: spec.stacked, ticks: { autoSkip: true, maxRotation: 0 } };
    state.chart = new Chart($('chart'), {
      type: spec.type,
      data: { labels: spec.labels, datasets: spec.datasets },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        animation: false,
        indexAxis: spec.horizontal ? 'y' : 'x',
        interaction: { mode: 'index', intersect: false },
        plugins: {
          legend: { display: spec.datasets.length > 1, position: 'bottom' },
          tooltip: {
            itemSort: (a, b) => b.raw - a.raw,
            callbacks: {
              footer: (tooltipItems) => {
                if (!spec.stacked || tooltipItems.length < 2) {
                  return '';
                }
                const sum = tooltipItems.reduce((acc, ti) => acc + ti.raw, 0);
                return `合計: ${format(round(sum, options.metric), options.metric)} ${unit(options.metric)}`;
              },
            },
          },
        },
        scales: spec.horizontal ? { x: valueAxis, y: categoryAxis } : { x: categoryAxis, y: valueAxis },
      },
    });

    const aggText = AGG_VIEWS.has(options.view) ? `・${AGG_LABELS[options.agg]}` : '';
    $('chartTitle').textContent = `${VIEW_LABELS[options.view]}：${METRIC_LABELS[options.metric]}${aggText}（${TARGET_LABELS[options.target]}）`;
    const notes = [];
    if (options.view === 'originMonth') {
      notes.push('持ち越し元の月ごとの表示は項目の選択に関係なく全項目を対象にします。');
    }
    if (AGG_VIEWS.has(options.view) && options.view !== 'originMonth') {
      notes.push('集計は日ごとの残をもとに計算します（例: 平均は期間内の 1 日あたりの残）。');
    }
    $('chartNote').textContent = notes.join(' ');
  }

  /* ---------- サマリカードと表 ---------- */

  function renderCards(days, options) {
    const { metric, target } = options;
    const cards = [];
    if (days.length > 0) {
      const last = days[days.length - 1];
      const prev = days.length > 1 ? days[days.length - 2] : null;
      const lastCount = dayTotal(last, 'count', target);
      const lastHours = dayTotal(last, 'hours', target);
      const values = days.map((d) => dayTotal(d, metric, target));
      const avg = aggregate(values, 'avg');
      const maxValue = Math.max(...values);
      const maxDay = days[values.indexOf(maxValue)];
      const minValue = Math.min(...values);
      const minDay = days[values.indexOf(minValue)];
      const diff = prev ? lastCount - dayTotal(prev, 'count', target) : 0;
      const sign = diff > 0 ? '+' : '';

      cards.push(['最新の残（件数）', `${format(lastCount, 'count')} 件`,
        `${last.date}（#${last.issue}）${prev ? `／前日比 ${sign}${format(diff, 'count')}` : ''}`]);
      cards.push(['最新の残り時間', `${format(round(lastHours, 'hours'), 'hours')} 時間`,
        `${format(round(lastHours * 60, 'minutes'), 'minutes')} 分`]);
      cards.push([`期間平均（${METRIC_LABELS[metric]}）`, `${format(round(avg, metric), metric)} ${unit(metric)}`,
        `${days.length} 日間`]);
      cards.push([`最大（${METRIC_LABELS[metric]}）`, `${format(round(maxValue, metric), metric)} ${unit(metric)}`,
        maxDay.date]);
      cards.push([`最小（${METRIC_LABELS[metric]}）`, `${format(round(minValue, metric), metric)} ${unit(metric)}`,
        minDay.date]);
    } else {
      cards.push(['データなし', '-', '期間や項目の選択を見直してください']);
    }

    const container = $('cards');
    container.replaceChildren(...cards.map(([label, value, sub]) => {
      const card = document.createElement('div');
      card.className = 'card';
      const l = document.createElement('div');
      l.className = 'label';
      l.textContent = label;
      const v = document.createElement('div');
      v.className = 'value';
      v.textContent = value;
      const s = document.createElement('div');
      s.className = 'sub';
      s.textContent = sub;
      card.append(l, v, s);
      return card;
    }));
  }

  function renderRanking(days, options) {
    const { metric, target } = options;
    const rows = selectedItemsOrdered().map((item) => {
      const values = days.map((d) => dayItemValue(d, item, metric, target));
      return {
        item,
        latest: values.length ? values[values.length - 1] : 0,
        avg: aggregate(values, 'avg'),
        max: aggregate(values, 'max'),
        min: aggregate(values, 'min'),
        sum: aggregate(values, 'sum'),
      };
    }).sort((a, b) => b.latest - a.latest || b.avg - a.avg);

    const cell = (value) => {
      const td = document.createElement('td');
      td.textContent = format(round(value, metric), metric);
      return td;
    };
    $('rankingBody').replaceChildren(...rows.map((row) => {
      const tr = document.createElement('tr');
      const name = document.createElement('td');
      const swatch = document.createElement('span');
      swatch.className = 'swatch';
      swatch.style.background = state.colors[row.item];
      name.append(swatch, ` ${row.item}`);
      tr.append(name, cell(row.latest), cell(row.avg), cell(row.max), cell(row.min), cell(row.sum));
      return tr;
    }));
  }

  /* ---------- 描画 ---------- */

  function render() {
    const options = currentOptions();
    $('agg').disabled = !AGG_VIEWS.has(options.view);
    const days = filteredDays(options);
    renderCards(days, options);
    renderChart(days, options);
    renderRanking(days, options);
  }

  function renderItemList() {
    const list = $('itemList');
    list.replaceChildren(...state.summary.items.map((item) => {
      const label = document.createElement('label');
      const checkbox = document.createElement('input');
      checkbox.type = 'checkbox';
      checkbox.checked = state.selected.has(item);
      checkbox.addEventListener('change', () => {
        if (checkbox.checked) {
          state.selected.add(item);
        } else {
          state.selected.delete(item);
        }
        render();
      });
      const swatch = document.createElement('span');
      swatch.className = 'swatch';
      swatch.style.background = state.colors[item];
      label.append(checkbox, swatch, item);
      return label;
    }));
  }

  function setPeriod(days) {
    const all = state.summary.days;
    if (all.length === 0) {
      return;
    }
    const last = all[all.length - 1].date;
    $('to').value = last;
    if (days === 0) {
      $('from').value = all[0].date;
      return;
    }
    const from = new Date(`${last}T00:00:00Z`);
    from.setUTCDate(from.getUTCDate() - (days - 1));
    $('from').value = from.toISOString().slice(0, 10);
  }

  function bindEvents() {
    for (const id of ['view', 'metric', 'agg', 'target', 'from', 'to']) {
      $(id).addEventListener('change', render);
    }
    for (const button of document.querySelectorAll('.presets button')) {
      button.addEventListener('click', () => {
        setPeriod(Number(button.dataset.days));
        render();
      });
    }
    $('selectAll').addEventListener('click', () => {
      state.selected = new Set(state.summary.items);
      renderItemList();
      render();
    });
    $('selectNone').addEventListener('click', () => {
      state.selected = new Set();
      renderItemList();
      render();
    });
  }

  function applyQuery() {
    const params = new URLSearchParams(window.location.search);
    for (const id of ['view', 'metric', 'agg', 'target']) {
      const value = params.get(id);
      const select = $(id);
      if (value && [...select.options].some((option) => option.value === value)) {
        select.value = value;
      }
    }
  }

  async function init() {
    applyQuery();
    try {
      const response = await fetch('data/summary.json', { cache: 'no-cache' });
      if (!response.ok) {
        throw new Error(`summary.json の取得に失敗しました（${response.status}）`);
      }
      state.summary = await response.json();
    } catch (error) {
      $('chartTitle').textContent = 'データを読み込めませんでした';
      $('chartNote').textContent = String(error);
      return;
    }

    state.summary.items.forEach((item, i) => {
      state.colors[item] = PALETTE[i % PALETTE.length];
    });
    state.selected = new Set(state.summary.items);
    const days = state.summary.days;
    if (days.length > 0) {
      $('meta').textContent = `最新: ${days[days.length - 1].date}（#${state.summary.latestIssue}）`;
      for (const id of ['from', 'to']) {
        $(id).min = days[0].date;
        $(id).max = days[days.length - 1].date;
      }
    }
    setPeriod(0);
    renderItemList();
    bindEvents();
    render();
  }

  init();
})();
