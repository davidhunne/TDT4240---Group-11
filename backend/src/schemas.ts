import { z, ZodError, ZodType } from "zod";
import { ValidationError } from "./errors";

/**
 * Parse a value against a schema, re-throwing zod issues as our own
 * ValidationError so the existing error middleware can serialize them.
 */
export function parseOrThrow<T>(schema: ZodType<T>, value: unknown, label: string): T {
  const result = schema.safeParse(value);
  if (result.success) return result.data;
  throw new ValidationError(formatZodError(result.error, label));
}

function formatZodError(error: ZodError, label: string): string {
  const issues = error.issues.map((issue) => {
    const path = issue.path.length > 0 ? issue.path.join(".") : label;
    return `${path}: ${issue.message}`;
  });
  return issues.join("; ");
}

const nonEmptyString = z.string().min(1);

/** Body for POST /api/players */
export const upsertPlayerBody = z.object({
  id: nonEmptyString,
  displayName: nonEmptyString,
});

/** Body for PATCH /api/players/:id */
export const updateDisplayNameBody = z.object({
  displayName: nonEmptyString,
});

/** Query for GET /api/players/leaderboard/top */
export const leaderboardQuery = z.object({
  limit: z.coerce.number().int().positive().max(100).optional(),
});

/** Body for POST /api/games */
export const createGameBody = z.object({
  playerId: nonEmptyString,
  displayName: nonEmptyString,
});

/** Body for POST /api/games/:gameId/join */
export const joinGameBody = z.object({
  playerId: nonEmptyString,
  displayName: nonEmptyString,
});

/** Body for POST /api/games/:gameId/leave and /start */
export const playerIdBody = z.object({
  playerId: nonEmptyString,
});

/** Body for POST /api/games/:gameId/move */
export const submitMoveBody = z.object({
  playerId: nonEmptyString,
  action: nonEmptyString,
  data: z.record(z.string(), z.unknown()).optional(),
});

/** Body for POST /api/games/:gameId/end */
export const endGameBody = z.object({
  winnerId: nonEmptyString.optional(),
});

/** Body for POST /api/games/:gameId/connection */
export const connectionBody = z.object({
  playerId: nonEmptyString,
  connected: z.boolean(),
});
