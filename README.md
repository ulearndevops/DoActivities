1. Install AWS CLI Version 2
AWS CLI Version 2 is the latest major version with enhanced features. Here’s how to install it:

Download the AWS CLI Version 2 installer:

bash
Copy code
curl "https://d1uj6qtbmh3dt5.cloudfront.net/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
For ARM-based systems, use:

bash
Copy code
curl "https://d1uj6qtbmh3dt5.cloudfront.net/awscli-exe-linux-aarch64.zip" -o "awscliv2.zip"
Unzip the installer:

bash
Copy code
unzip awscliv2.zip
Run the installer:

bash
Copy code
sudo ./aws/install
Verify the installation:

bash
Copy code
aws --version
