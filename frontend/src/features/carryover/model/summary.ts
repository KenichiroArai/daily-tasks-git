import { z } from 'zod';

/**
 * 件数と残り時間の集計値（summary.json の total / byItem / byOriginMonth の値）
 */
export const statSchema = z.object({
  count: z.number(),
  minutes: z.number(),
  checkedCount: z.number(),
  checkedMinutes: z.number(),
});

/**
 * 1 日分の集計（その日の Issue の残）
 */
export const daySchema = z.object({
  date: z.string().regex(/^\d{4}-\d{2}-\d{2}$/),
  issue: z.number().int(),
  declaredCount: z.number().nullable(),
  total: statSchema,
  byItem: z.record(z.string(), statSchema),
  byOriginMonth: z.record(z.string(), statSchema),
});

/**
 * 画面用の日別集計（docs/data/summary.json）
 */
export const summarySchema = z.object({
  latestIssue: z.number().int(),
  items: z.array(z.string()),
  days: z.array(daySchema),
});

export type Stat = z.infer<typeof statSchema>;
export type Day = z.infer<typeof daySchema>;
export type Summary = z.infer<typeof summarySchema>;
