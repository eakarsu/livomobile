import { createHmac } from "node:crypto";
import { z } from "zod";

const responseSchema = z.object({
  receiptId: z.string().min(1).max(200),
  artifactUri: z.string().url().max(2048).optional(),
  artifactSha256: z.string().regex(/^[a-f0-9]{64}$/).optional(),
}).strict();
const wait = (milliseconds) => new Promise((resolve) => setTimeout(resolve, milliseconds));

export async function invokeDocumentProvider(config, payload, idempotencyKey, fetchImplementation = fetch) {
  const body = JSON.stringify(payload);
  const signature = createHmac("sha256", config.documentProviderSecret).update(body).digest("hex");
  const attempts = [];
  for (let attempt = 1; attempt <= config.documentProviderMaxAttempts; attempt += 1) {
    try {
      const response = await fetchImplementation(config.documentProviderUrl, {
        method: "POST",
        headers: {
          "Content-Type": "application/json", "Idempotency-Key": idempotencyKey,
          "X-Livo-Document-Signature-SHA256": signature,
        },
        body, signal: AbortSignal.timeout(config.documentProviderTimeoutMs),
      });
      if (!response.ok) {
        const rejected = response.status >= 400 && response.status < 500 && ![408, 429].includes(response.status);
        attempts.push({ attempt, outcome: "FAILED", errorCode: rejected ? "PROVIDER_REJECTED" : "PROVIDER_HTTP_ERROR", providerStatus: response.status });
        if (rejected) return { ok: false, attempts };
      } else {
        const result = responseSchema.safeParse(await response.json());
        if (result.success) {
          attempts.push({ attempt, outcome: "SUCCEEDED", errorCode: null, providerStatus: response.status });
          return { ok: true, ...result.data, attempts };
        }
        attempts.push({ attempt, outcome: "FAILED", errorCode: "INVALID_PROVIDER_RESPONSE", providerStatus: response.status });
      }
    } catch (error) {
      attempts.push({ attempt, outcome: "FAILED", errorCode: error?.name === "TimeoutError" ? "PROVIDER_TIMEOUT" : "PROVIDER_UNAVAILABLE", providerStatus: null });
    }
    if (attempt < config.documentProviderMaxAttempts) await wait(Math.min(25 * 2 ** (attempt - 1), 200));
  }
  return { ok: false, attempts };
}
