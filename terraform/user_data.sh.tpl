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

# ── 2. Clonar el repositorio (rama feature/bmdev) ─────────────────────
cd /home/ubuntu
sudo -u ubuntu git clone -b feature/bmdev https://github.com/DylanMarchant24/smartLogixExp3FullStack.git
cd smartLogixExp3FullStack

# ── 3. Crear el .env con los valores inyectados por Terraform ─────────
cat > .env <<EOF
MYSQL_ROOT_PASSWORD=${mysql_root_password}
AZURE_CLIENT_ID=${azure_client_id}
AZURE_TENANT_ID=${azure_tenant_id}
FRONTEND_URL=${frontend_url}
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
