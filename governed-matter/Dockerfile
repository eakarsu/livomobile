FROM node:22-bookworm-slim AS dependencies
WORKDIR /app
RUN apt-get update && apt-get install --yes --no-install-recommends python3 make g++ \
  && rm -rf /var/lib/apt/lists/*
COPY package.json package-lock.json ./
RUN npm ci --omit=dev && npm cache clean --force

FROM node:22-bookworm-slim
ENV NODE_ENV=production
WORKDIR /app
RUN groupadd --system --gid 10001 app && useradd --system --uid 10001 --gid app app \
  && mkdir -p /var/lib/livomobile && chown app:app /var/lib/livomobile
COPY package.json package-lock.json ./
COPY --from=dependencies /app/node_modules ./node_modules
COPY src ./src
COPY migrations ./migrations
COPY scripts ./scripts
USER app
EXPOSE 3030
VOLUME ["/var/lib/livomobile"]
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD node -e "fetch('http://127.0.0.1:3030/health/live').then(r=>{if(!r.ok)process.exit(1)}).catch(()=>process.exit(1))"
CMD ["node", "src/index.mjs"]
