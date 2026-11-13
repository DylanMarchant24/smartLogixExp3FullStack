output "backend_public_ip" {
  value = aws_eip.backend_eip.public_ip
}

output "bff_url" {
  value = "http://${aws_eip.backend_eip.public_ip}:8080"
}

output "eureka_url" {
  value = "http://${aws_eip.backend_eip.public_ip}:8761"
}

output "ssh_command" {
  value = "ssh -i labsuser.pem ubuntu@${aws_eip.backend_eip.public_ip}"
}

output "rabbitmq_url" {
  description = "RabbitMQ Management UI - Nodo 1"
  value       = "http://${aws_eip.backend_eip.public_ip}:15672"
}

output "rabbitmq_node2_url" {
  description = "RabbitMQ Management UI - Nodo 2 (Cluster)"
  value       = "http://${aws_eip.backend_eip.public_ip}:15673"
}

output "kafka_ui_url" {
  description = "Kafka UI para inspeccionar Brokers, Topics, Particiones y Consumer Groups"
  value       = "http://${aws_eip.backend_eip.public_ip}:8095"
}

output "api_gateway_url" {
  description = "URL del API Gateway con validacion JWT OAuth2 Resource Server"
  value       = "http://${aws_eip.backend_eip.public_ip}:8085"
}

output "frontend_url" {
  description = "URL publica segura HTTPS del frontend mediante ngrok"
  value       = "https://${var.ngrok_domain}"
}

output "swagger_rabbit_admin_url" {
  description = "URL interactiva de Swagger UI para probar y administrar RabbitMQ (ms-rabbit-admin)"
  value       = "http://${aws_eip.backend_eip.public_ip}:8093/swagger-ui/index.html"
}

output "auditoria_kafka_topics_url" {
  description = "Endpoint REST de administracion de topicos Kafka en ms-auditoria"
  value       = "http://${aws_eip.backend_eip.public_ip}:8094/api/auditoria/kafka/topics"
}
