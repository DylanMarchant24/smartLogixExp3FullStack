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
  value = "ssh -i smartlogix-key ubuntu@${aws_eip.backend_eip.public_ip}"
}
