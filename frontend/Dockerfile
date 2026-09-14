# ==============================================================================
# Dockerfile del Frontend SmartLogix (React - Create React App)
# Multi-stage: compila con Node y sirve el build estatico con Nginx sobre HTTPS
# (certificado autofirmado), requerido porque MSAL usa Web Crypto API, que
# los navegadores solo exponen en contextos seguros (HTTPS o localhost).
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

# ── Etapa 2: servir con Nginx + HTTPS autofirmado ──────────────────
FROM nginx:1.27-alpine

ARG SERVER_IP=127.0.0.1

RUN apk add --no-cache openssl \
    && mkdir -p /etc/nginx/ssl \
    && openssl req -x509 -nodes -days 365 \
        -newkey rsa:2048 \
        -keyout /etc/nginx/ssl/selfsigned.key \
        -out /etc/nginx/ssl/selfsigned.crt \
        -subj "/C=CL/ST=RM/L=Santiago/O=SmartLogix/CN=${SERVER_IP}" \
        -addext "subjectAltName=IP:${SERVER_IP}"

COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]
