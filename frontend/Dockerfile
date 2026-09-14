# ==============================================================================
# Dockerfile del Frontend SmartLogix (React - Create React App)
# Multi-stage: compila con Node y sirve el build estatico con Nginx.
# Las variables REACT_APP_* deben pasarse como build-args, ya que CRA las
# incrusta en el bundle en tiempo de build, no en tiempo de ejecucion.
# ==============================================================================

# ── Etapa 1: build con Node ──────────────────────────────────────────
FROM node:22-alpine AS build
WORKDIR /app

ARG REACT_APP_AZURE_CLIENT_ID
ARG REACT_APP_AZURE_TENANT_ID
ARG REACT_APP_AZURE_REDIRECT_URI
ARG REACT_APP_API_BASE_URL

ENV REACT_APP_AZURE_CLIENT_ID=$REACT_APP_AZURE_CLIENT_ID
ENV REACT_APP_AZURE_TENANT_ID=$REACT_APP_AZURE_TENANT_ID
ENV REACT_APP_AZURE_REDIRECT_URI=$REACT_APP_AZURE_REDIRECT_URI
ENV REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL

COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

# ── Etapa 2: servir con Nginx ──────────────────────────────────────
FROM nginx:1.27-alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]
