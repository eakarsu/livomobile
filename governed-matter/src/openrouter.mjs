export class OpenRouterError extends Error {
  constructor(code, message, status = 502) {
    super(message);
    this.name = "OpenRouterError";
    this.code = code;
    this.status = status;
  }
}

export async function invokeOpenRouter(config, prompt, fetchImplementation = fetch) {
  if (!config.openRouterApiKey) throw new OpenRouterError("OPENROUTER_NOT_CONFIGURED", "OpenRouter is not configured", 503);
  const started = Date.now();
  let response;
  try {
    response = await fetchImplementation(`${config.openRouterBaseUrl}/chat/completions`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${config.openRouterApiKey}`,
        "Content-Type": "application/json",
        "HTTP-Referer": "https://github.com/livomobile/livomobile",
        "X-Title": "LivoMobile Governed Matter",
      },
      body: JSON.stringify({
        model: config.openRouterModel,
        messages: [
          { role: "system", content: "You are a concise legal-document workflow assistant. Do not provide legal advice; identify operational next steps and uncertainties." },
          { role: "user", content: prompt },
        ],
        temperature: 0.2,
        max_tokens: 800,
      }),
      signal: AbortSignal.timeout(config.openRouterTimeoutMs),
    });
  } catch (error) {
    throw new OpenRouterError(error?.name === "TimeoutError" ? "OPENROUTER_TIMEOUT" : "OPENROUTER_UNAVAILABLE",
      "The AI provider is unavailable");
  }
  if (!response.ok) throw new OpenRouterError(`OPENROUTER_HTTP_${response.status}`, "The AI provider rejected the request");
  let body;
  try { body = await response.json(); } catch { throw new OpenRouterError("OPENROUTER_INVALID_JSON", "The AI provider returned invalid JSON"); }
  const receipt = typeof body?.id === "string" ? body.id : null;
  const model = typeof body?.model === "string" ? body.model : null;
  const output = body?.choices?.[0]?.message?.content;
  const finishReason = body?.choices?.[0]?.finish_reason;
  if (!receipt || !model || typeof output !== "string" || output.length === 0) {
    throw new OpenRouterError("OPENROUTER_INVALID_RESPONSE", "The AI provider returned incomplete evidence");
  }
  return { receipt, model, output, finishReason: typeof finishReason === "string" ? finishReason : null, latencyMs: Date.now() - started };
}
