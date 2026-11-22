// Maps every error to application/problem+json with a stable code. No internals leak.
import type { FastifyError, FastifyInstance } from 'fastify';
import { hasZodFastifySchemaValidationErrors } from 'fastify-type-provider-zod';
import { AppError } from '../shared/errors.js';

const PROBLEM_TYPE = 'application/problem+json';
const HTTP_BAD_REQUEST = 400;
const HTTP_TOO_MANY = 429;
const HTTP_INTERNAL = 500;

export function registerErrorHandler(app: FastifyInstance): void {
  app.setErrorHandler((error: FastifyError, request, reply) => {
    if (error instanceof AppError) {
      return reply
        .code(error.status)
        .type(PROBLEM_TYPE)
        .send({ status: error.status, code: error.code });
    }
    if (hasZodFastifySchemaValidationErrors(error)) {
      const fields = error.validation.map((issue) => issue.instancePath);
      return reply
        .code(HTTP_BAD_REQUEST)
        .type(PROBLEM_TYPE)
        .send({ status: HTTP_BAD_REQUEST, code: 'VALIDATION_FAILED', fields });
    }
    if (error.statusCode === HTTP_TOO_MANY) {
      return reply
        .code(HTTP_TOO_MANY)
        .type(PROBLEM_TYPE)
        .send({ status: HTTP_TOO_MANY, code: 'RATE_LIMITED' });
    }
    if (error.statusCode !== undefined && error.statusCode < HTTP_INTERNAL) {
      return reply
        .code(error.statusCode)
        .type(PROBLEM_TYPE)
        .send({ status: error.statusCode, code: 'VALIDATION_FAILED' });
    }
    request.log.error({ err: error }, 'unhandled error');
    return reply
      .code(HTTP_INTERNAL)
      .type(PROBLEM_TYPE)
      .send({ status: HTTP_INTERNAL, code: 'INTERNAL_ERROR' });
  });

  app.setNotFoundHandler((_request, reply) =>
    reply.code(404).type(PROBLEM_TYPE).send({ status: 404, code: 'NOT_FOUND' }),
  );
}
