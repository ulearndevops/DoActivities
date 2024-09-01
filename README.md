ethod 2: Building Python 3.9 from Source
Install Prerequisites:

bash
Copy code
sudo yum groupinstall "Development Tools" -y
sudo yum install openssl-devel bzip2-devel libffi-devel -y
Download Python 3.9 Source:

Navigate to the Python official website to get the latest source code URL or use the command below for Python 3.9.13 (replace with the latest version as needed):

bash
Copy code
wget https://www.python.org/ftp/python/3.9.13/Python-3.9.13.tgz
Extract the Source Code:

bash
Copy code
tar xzf Python-3.9.13.tgz
Build and Install Python:

bash
Copy code
cd Python-3.9.13
./configure --enable-optimizations
make
sudo make altinstall
altinstall prevents overwriting the default python binary.
Verify the Installation:

bash
Copy code
python3.9 --version
