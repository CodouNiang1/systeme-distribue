# ── Build ─────────────────────────────────────────────────────────────────────
FROM node:18-alpine AS builder
WORKDIR /app

COPY package.json ./
RUN npm install

COPY . .
RUN npm run build

# ── Image finale (Nginx) ───────────────────────────────────────────────────────
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html

# Config Nginx pour React Router + proxy API
COPY nginx-frontend.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
