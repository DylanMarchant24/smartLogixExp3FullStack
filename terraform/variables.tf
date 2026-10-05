variable "aws_region" {
  description = "Región de AWS (AWS Academy suele usar us-east-1)"
  type        = string
  default     = "us-east-1"
}

variable "instance_type" {
  description = "Tipo de instancia EC2. t3.large recomendado para correr 13 contenedores + MySQL."
  type        = string
  default     = "t3.large"
}

variable "ssh_cidr" {
  description = "CIDR permitido para SSH/Eureka/Gateway/debug. Usar tu IP/32 si corres Terraform local, o 0.0.0.0/0 si corres desde GitHub Actions."
  type        = string
}

variable "key_pair_name" {
  description = "Nombre del key pair en AWS (en AWS Academy se usa por defecto vockey)"
  type        = string
  default     = "vockey"
}

variable "mysql_root_password" {
  description = "Password root de MySQL"
  type        = string
  sensitive   = true
}

variable "azure_client_id" {
  description = "Application (client) ID de tu app registrada en Azure Entra ID"
  type        = string
  sensitive   = true
}

variable "azure_tenant_id" {
  description = "Directory (tenant) ID de tu app registrada en Azure Entra ID"
  type        = string
  sensitive   = true
}

variable "frontend_url" {
  description = "URL donde se sirve el frontend (para CORS del API Gateway)"
  type        = string
  default     = "http://localhost:3000"
}

variable "ngrok_authtoken" {
  description = "Authtoken de ngrok para exponer el frontend con HTTPS público"
  type        = string
  sensitive   = true
  default     = ""
}

variable "ngrok_domain" {
  description = "Dominio estático de ngrok asignado a tu cuenta"
  type        = string
  default     = "citric-proactive-rounding.ngrok-free.dev"
}
