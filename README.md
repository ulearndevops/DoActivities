provider "azurerm" {
  features {}
}

# Variables for existing resources
variable "resource_group_name" {
  description = "The name of the resource group where the VM will be deployed"
  type        = string
}

variable "image_resource_group_name" {
  description = "The name of the resource group where the image gallery is located"
  type        = string
}

variable "image_gallery_name" {
  description = "The name of the shared image gallery"
  type        = string
}

variable "image_definition_name" {
  description = "The name of the image definition in the gallery"
  type        = string
}

variable "image_version" {
  description = "The version of the image in the gallery"
  type        = string
}

variable "vnet_name" {
  description = "The name of the virtual network where the VM will be deployed"
  type        = string
}

variable "subnet_name" {
  description = "The name of the subnet where the VM will be deployed"
  type        = string
}

variable "vm_size" {
  description = "The size of the VM"
  type        = string
  default     = "Standard_DS1_v2"
}

variable "vm_name" {
  description = "The name of the VM"
  type        = string
}

variable "admin_username" {
  description = "Admin username for the VM"
  type        = string
}

variable "admin_password" {
  description = "Admin password for the VM"
  type        = string
  sensitive   = true
}

# Data source to get existing resources
data "azurerm_resource_group" "vm_rg" {
  name = var.resource_group_name
}

data "azurerm_virtual_network" "vnet" {
  name                = var.vnet_name
  resource_group_name = var.resource_group_name
}

data "azurerm_subnet" "subnet" {
  name                 = var.subnet_name
  virtual_network_name = data.azurerm_virtual_network.vnet.name
  resource_group_name  = var.resource_group_name
}

# Data source to get the Ubuntu image from the Azure Compute Gallery
data "azurerm_shared_image" "ubuntu_image" {
  name                = var.image_definition_name
  gallery_name        = var.image_gallery_name
  resource_group_name = var.image_resource_group_name
  version             = var.image_version
}

# Network Interface for the VM
resource "azurerm_network_interface" "example_nic" {
  name                = "${var.vm_name}-nic"
  location            = data.azurerm_resource_group.vm_rg.location
  resource_group_name = data.azurerm_resource_group.vm_rg.name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = data.azurerm_subnet.subnet.id
    private_ip_address_allocation = "Dynamic"
  }
}

# Creating the Virtual Machine
resource "azurerm_linux_virtual_machine" "example_vm" {
  name                = var.vm_name
  resource_group_name = data.azurerm_resource_group.vm_rg.name
  location            = data.azurerm_resource_group.vm_rg.location
  size                = var.vm_size
  admin_username      = var.admin_username
  admin_password      = var.admin_password

  network_interface_ids = [
    azurerm_network_interface.example_nic.id
  ]

  source_image_id = data.azurerm_shared_image.ubuntu_image.id

  os_disk {
    caching              = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

  identity {
    type = "SystemAssigned"
  }

  admin_ssh_key {
    username   = var.admin_username
    public_key = file("~/.ssh/id_rsa.pub")
  }

  tags = {
    environment = "internal"
  }
}

# Output the private IP address
output "private_ip" {
  description = "The private IP address of the virtual machine"
  value       = azurerm_network_interface.example_nic.private_ip_address
}
