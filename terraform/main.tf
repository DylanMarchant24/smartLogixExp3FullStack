terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_security_group" "smartlogix_sg" {
  name        = "smartlogix-sg"
  description = "SmartLogix - reglas de acceso backend (Docker)"
  vpc_id      = data.aws_vpc.default.id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.ssh_cidr]
  }

  ingress {
    description = "BFF"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Eureka dashboard"
    from_port   = 8761
    to_port     = 8761
    protocol    = "tcp"
    cidr_blocks = [var.ssh_cidr]
  }

  ingress {
    description = "Microservicios internos (debug opcional)"
    from_port   = 8081
    to_port     = 8092
    protocol    = "tcp"
    cidr_blocks = [var.ssh_cidr]
  }

  ingress {
    description = "Frontend HTTP (redirige a HTTPS)"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Frontend HTTPS (autofirmado)"
    from_port   = 3443
    to_port     = 3443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "API Gateway interno (Spring Cloud Gateway) - integracion desde AWS API Gateway"
    from_port   = 8085
    to_port     = 8085
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "smartlogix-sg"
  }
}

resource "aws_key_pair" "smartlogix_key" {
  key_name   = var.key_pair_name
  public_key = var.public_key
}

resource "aws_instance" "backend" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  key_name               = aws_key_pair.smartlogix_key.key_name
  subnet_id              = data.aws_subnets.default.ids[0]
  vpc_security_group_ids = [aws_security_group.smartlogix_sg.id]

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  user_data = templatefile("${path.module}/user_data.sh.tpl", {
    mysql_root_password = var.mysql_root_password
    azure_client_id     = var.azure_client_id
    azure_tenant_id     = var.azure_tenant_id
    frontend_url        = var.frontend_url
  })

  tags = {
    Name = "smartlogix-backend"
  }
}

resource "aws_eip" "backend_eip" {
  instance = aws_instance.backend.id
  domain   = "vpc"

  tags = {
    Name = "smartlogix-backend-eip"
  }
}

# ==============================================================================
# AWS API Gateway (servicio administrado) - requisito de la pauta.
# HTTP API que expone el api-gateway interno (Spring Cloud Gateway, puerto 8085
# en la EC2) bajo una URL HTTPS publica gestionada por AWS, con certificado
# valido emitido por AWS (sin certificados autofirmados, sin mixed content).
# Integracion tipo HTTP_PROXY: reenvia cualquier metodo/ruta 1:1 hacia
# http://<EIP>:8085/{proxy} dentro de la instancia EC2.
# ==============================================================================

resource "aws_apigatewayv2_api" "smartlogix_api" {
  name          = "smartlogix-api-gateway"
  protocol_type = "HTTP"

  cors_configuration {
    allow_origins = [var.frontend_url]
    allow_methods = ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"]
    allow_headers = ["Authorization", "Content-Type"]
    max_age       = 300
  }

  tags = {
    Name = "smartlogix-api-gateway"
  }
}

resource "aws_apigatewayv2_integration" "smartlogix_backend_integration" {
  api_id                 = aws_apigatewayv2_api.smartlogix_api.id
  integration_type       = "HTTP_PROXY"
  integration_method     = "ANY"
  integration_uri        = "http://${aws_eip.backend_eip.public_ip}:8085/{proxy}"
  payload_format_version = "1.0"
  connection_type        = "INTERNET"
  timeout_milliseconds   = 29000
}

resource "aws_apigatewayv2_route" "smartlogix_proxy_route" {
  api_id    = aws_apigatewayv2_api.smartlogix_api.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.smartlogix_backend_integration.id}"
}

resource "aws_apigatewayv2_route" "smartlogix_root_route" {
  api_id    = aws_apigatewayv2_api.smartlogix_api.id
  route_key = "ANY /"
  target    = "integrations/${aws_apigatewayv2_integration.smartlogix_backend_integration.id}"
}

resource "aws_apigatewayv2_stage" "smartlogix_stage" {
  api_id      = aws_apigatewayv2_api.smartlogix_api.id
  name        = "$default"
  auto_deploy = true
}
