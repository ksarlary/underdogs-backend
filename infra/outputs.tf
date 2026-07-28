output "fqdn" {
  description = "Nom de domaine public de la stack."
  value       = azurerm_public_ip.main.fqdn
}

output "site_address" {
  description = "Valeur à reporter dans SITE_ADDRESS du fichier .env."
  value       = azurerm_public_ip.main.fqdn
}

output "public_ip" {
  description = "IP publique de la VM."
  value       = azurerm_public_ip.main.ip_address
}

output "ssh_command" {
  description = "Commande de connexion à la VM."
  value       = "ssh ${var.admin_username}@${azurerm_public_ip.main.fqdn}"
}
