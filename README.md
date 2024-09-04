  If you don't want to pass the CIDR ranges from the command line when running `terraform apply`, you can define the variables directly in your Terraform configuration, but allow them to be easily overridden via a `terraform.tfvars` file or environment variables. Here's how you can do it:

### Option 1: Use `terraform.tfvars` File

You can create a `terraform.tfvars` file in the same directory as your Terraform configuration. This file will automatically be loaded by Terraform and will contain the CIDR ranges:

**terraform.tfvars:**

```hcl
vnet_cidr  = "10.0.0.0/16"
subnet_cidr = "10.0.1.0/24"
```

### Option 2: Set Default Values in the `.tf` File

If you prefer not to use a `terraform.tfvars` file and don't want to pass the values during `terraform apply`, you can define the default values directly in the `.tf` file as follows:

```hcl
# Define CIDR variables with default values
variable "vnet_cidr" {
  description = "The CIDR range for the Virtual Network."
  default     = "10.0.0.0/16"  # Default CIDR range for the VNet
}

variable "subnet_cidr" {
  description = "The CIDR range for the Subnet."
  default     = "10.0.1.0/24"  # Default CIDR range for the Subnet
}
```

### Terraform Configuration Example:

```hcl
provider "azurerm" {
  features {}
}

variable "location" {
  description = "Azure region to deploy resources."
  default     = "East US"
}

variable "resource_group_name" {
  description = "The name of the resource group."
  default     = "myResourceGroup"
}

variable "vm_name" {
  description = "The name of the Virtual Machine."
  default     = "myVM"
}

variable "vm_size" {
  description = "The size of the Virtual Machine."
  default     = "Standard_DS1_v2"
}

variable "admin_username" {
  description = "The admin username for the VM."
  default     = "azureuser"
}

variable "admin_password" {
  description = "The admin password for the VM."
  sensitive   = true
}

variable "ssh_public_key" {
  description = "The SSH public key for the VM."
}

variable "vnet_name" {
  description = "The name of the Virtual Network."
  default     = "myVnet"
}

variable "vnet_cidr" {
  description = "The CIDR range for the Virtual Network."
  default     = "10.0.0.0/16"  # Default value, can be overridden if needed
}

variable "subnet_name" {
  description = "The name of the Subnet."
  default     = "mySubnet"
}

variable "subnet_cidr" {
  description = "The CIDR range for the Subnet."
  default     = "10.0.1.0/24"  # Default value, can be overridden if needed
}

variable "image_gallery_name" {
  description = "The name of the Shared Image Gallery."
}

variable "image_definition_name" {
  description = "The name of the image definition."
}

variable "image_version" {
  description = "The version of the image to use."
}

variable "tags" {
  description = "Tags to apply to resources."
  type        = map(string)
  default     = {
    environment = "production"
    department  = "IT"
  }
}

resource "azurerm_resource_group" "main" {
  name     = var.resource_group_name
  location = var.location

  tags = var.tags
}

resource "azurerm_virtual_network" "main" {
  name                = var.vnet_name
  address_space       = [var.vnet_cidr]
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name

  tags = var.tags
}

resource "azurerm_subnet" "main" {
  name                 = var.subnet_name
  resource_group_name  = azurerm_resource_group.main.name
  virtual_network_name = azurerm_virtual_network.main.name
  address_prefixes     = [var.subnet_cidr]

  tags = var.tags
}

resource "azurerm_network_interface" "main" {
  name                = "${var.vm_name}-nic"
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name

  ip_configuration {
    name                          = "internal"
    subnet_id                     = azurerm_subnet.main.id
    private_ip_address_allocation = "Dynamic"
  }

  tags = var.tags
}

data "azurerm_shared_image" "main" {
  name                = var.image_definition_name
  gallery_name        = var.image_gallery_name
  resource_group_name = azurerm_resource_group.main.name
  version             = var.image_version
}

resource "azurerm_linux_virtual_machine" "main" {
  name                = var.vm_name
  location            = azurerm_resource_group.main.location
  resource_group_name = azurerm_resource_group.main.name
  network_interface_ids = [
    azurerm_network_interface.main.id
  ]
  size                = var.vm_size
  admin_username      = var.admin_username
  disable_password_authentication = false

  admin_password      = var.admin_password

  os_disk {
    name              = "${var.vm_name}-osdisk"
    caching           = "ReadWrite"
    storage_account_type = "Standard_LRS"
  }

  source_image_id = data.azurerm_shared_image.main.id

  computer_name = var.vm_name

  ssh_key {
    key_data = var.ssh_public_key
  }

  tags = var.tags
}

output "vm_id" {
  value = azurerm_linux_virtual_machine.main.id
}

output "private_ip" {
  value = azurerm_network_interface.main.private_ip_address
}
```

### Summary:
- **No Need for CLI Parameters**: The CIDR ranges are defined as default values within the Terraform configuration, so you don't need to pass them via the command line.
- **Flexibility**: You can still override these defaults using a `terraform.tfvars` file or by setting environment variables if needed, without hardcoding values in the code.

This setup makes the configuration more flexible while ensuring the CIDR ranges are not hardcoded and can be easily changed if necessary.
