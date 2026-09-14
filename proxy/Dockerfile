# ==============================================================================
# Reverse proxy HTTPS delante del api-gateway.
# Termina TLS con un certificado autofirmado (mismo enfoque que frontend/) y
# reenvia el trafico por HTTP hacia api-gateway:8085 dentro de la red interna
# de Docker. Existe para evitar bloqueos de "mixed content" en el navegador,
# ya que el frontend se sirve por HTTPS y no puede llamar a un backend HTTP.
# ==============================================================================
FROM nginx:1.27-alpine

ARG SERVER_IP=98.88.0.12

RUN apk add --no-cache openssl \
    && mkdir -p /etc/nginx/ssl \
    && openssl req -x509 -nodes -days 365 \
        -newkey rsa:2048 \
        -keyout /etc/nginx/ssl/selfsigned.key \
        -out /etc/nginx/ssl/selfsigned.crt \
        -subj "/C=CL/ST=RM/L=Santiago/O=SmartLogix/CN=${SERVER_IP}" \
        -addext "subjectAltName=IP:${SERVER_IP}"

COPY nginx.conf /etc/nginx/conf.d/default.conf
# Elimina el server block default (puerto 80) que trae la imagen base,
# ya que este proxy solo debe escuchar en 8443.
RUN rm -f /etc/nginx/conf.d/default.conf.bak 2>/dev/null || true

EXPOSE 8443
CMD ["nginx", "-g", "daemon off;"]
