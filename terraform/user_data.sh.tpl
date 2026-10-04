#!/bin/bash
set -e
LOG=/var/log/smartlogix-setup.log
exec > >(tee -a $LOG) 2>&1

echo "=== SmartLogix - Aprovisionamiento Docker iniciado $(date) ==="

# ── 1. Instalar Docker y Docker Compose ─────────────────────────
apt-get update -y
apt-get install -y ca-certificates curl gnupg git

install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

apt-get update -y
apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

systemctl enable docker
systemctl start docker
usermod -aG docker ubuntu

# ── 2. Clonar el repositorio (rama develop) ───────────────────────────
cd /home/ubuntu
sudo -u ubuntu git clone -b develop https://github.com/DylanMarchant24/smartLogixExp3FullStack.git
cd smartLogixExp3FullStack

# ── 3. Crear el .env con los valores inyectados por Terraform ─────────
# Obtener IP pública dinámica de la instancia en AWS
TOKEN=$(curl -s -m 5 -X PUT "http://169.254.169.254/latest/api/token" -H "X-aws-ec2-metadata-token-ttl-seconds: 60" || true)
PUBLIC_IP=""
if [ -n "$TOKEN" ]; then
  PUBLIC_IP=$(curl -s -m 5 -H "X-aws-ec2-metadata-token: $TOKEN" http://169.254.169.254/latest/meta-data/public-ipv4 || true)
fi
if [ -z "$PUBLIC_IP" ]; then
  PUBLIC_IP=$(curl -s -m 5 ifconfig.me || curl -s -m 5 icanhazip.com || echo "localhost")
fi
echo "IP publica detectada: $PUBLIC_IP"

cat > .env <<EOF
MYSQL_ROOT_PASSWORD=${mysql_root_password}
AZURE_CLIENT_ID=${azure_client_id}
AZURE_TENANT_ID=${azure_tenant_id}
FRONTEND_URL=http://$PUBLIC_IP:3000
API_BASE_URL=http://$PUBLIC_IP:8080
RABBITMQ_DEFAULT_USER=smartlogix
RABBITMQ_DEFAULT_PASS=smartlogix123
EOF
chown ubuntu:ubuntu .env
chmod 600 .env

# ── 4. Construir y levantar todo con Docker Compose ───────────────
echo "=== Construyendo imágenes (puede tardar varios minutos) ==="
docker compose build

echo "=== Levantando servicios ==="
docker compose up -d

echo "=== SmartLogix - Aprovisionamiento completado $(date) ==="
echo "Ver estado con: docker compose ps"
echo "Ver logs con: docker compose logs -f <servicio>"
