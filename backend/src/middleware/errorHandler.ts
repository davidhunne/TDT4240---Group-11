import { NextFunction, Request, Response } from "express";
import { AppError, NotFoundError } from "../errors";

export function notFoundHandler(req: Request, _res: Response, next: NextFunction): void {
  next(new NotFoundError(`Route not found: ${req.method} ${req.originalUrl}`));
}

export function errorHandler(
  err: unknown,
  req: Request,
  res: Response,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  _next: NextFunction,
): void {
  if (err instanceof AppError) {
    res.status(err.statusCode).json({
      error: { code: err.code, message: err.message },
    });
    return;
  }

  const message = err instanceof Error ? err.message : "Internal server error";
  console.error(
    `[error] ${req.method} ${req.originalUrl}`,
    err instanceof Error ? err.stack : err,
  );

  res.status(500).json({
    error: { code: "INTERNAL_ERROR", message },
  });
}
