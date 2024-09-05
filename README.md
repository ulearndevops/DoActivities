Here's a Terraform script to create a VM using an existing Ubuntu image from the Azure Compute Gallery (shared across resource groups), while deploying the VM into an existing resource group, VNet, and subnet. This script assumes you already have all these resources and just want to reference them without creating new ones.

Terraform Script
hcl
Copy code
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
}

# Data sources to get existing resources
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

# Creating the virtual machine
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
}

# Network interface for the VM
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
Explanation:
Existing Resources: The script uses data blocks to reference the existing resource group, VNet, subnet, and shared image.
Shared Image Gallery: The azurerm_shared_image data block is used to fetch the Ubuntu image from the Azure Compute Gallery in another resource group.
VM Deployment: The azurerm_linux_virtual_machine resource block deploys the VM into the existing resources, with the specified VM size, admin username, and password.
Network Interface: A network interface is created and connected to the specified subnet, with a dynamic private IP for internal access.
Variables:
Replace the placeholder values with actual values for resource group names, image names, versions, VNet, subnet, etc.
Let me know if you need further customization.
