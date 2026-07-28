variable "subscription_id" {
  description = "Identifiant de l'abonnement Azure (az account show --query id -o tsv)."
  type        = string
}

variable "project" {
  description = "Préfixe appliqué au nom de toutes les ressources."
  type        = string
  default     = "underdogs"
}

variable "environment" {
  description = "Environnement déployé. Sert à isoler deux stacks complètes."
  type        = string

  validation {
    condition     = contains(["dev", "prod"], var.environment)
    error_message = "L'environnement doit être dev ou prod."
  }
}

variable "location" {
  description = "Région Azure."
  type        = string
  default     = "francecentral"
}

variable "dns_label" {
  description = "Label DNS de l'IP publique. Doit être unique dans la région."
  type        = string
}

variable "vm_size" {
  description = "Taille de la VM. B2s (2 vCPU / 4 Go) est le minimum confortable pour la stack complète."
  type        = string
  default     = "Standard_B2s"
}

variable "os_disk_size_gb" {
  description = "Taille du disque système."
  type        = number
  default     = 32
}

variable "admin_username" {
  description = "Utilisateur SSH créé sur la VM."
  type        = string
  default     = "underdogs"
}

variable "ssh_public_key_path" {
  description = "Chemin vers la clé publique SSH autorisée à se connecter."
  type        = string
  default     = "~/.ssh/id_rsa.pub"
}

variable "ssh_source_address" {
  description = "Adresse autorisée à ouvrir SSH. Mettre son IP publique plutôt que * en prod."
  type        = string
  default     = "*"
}
