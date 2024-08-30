
To fix the `GLIBC_2.35 not found` error in Amazon Linux, you'll likely need to take one of the following approaches, as Amazon Linux typically uses an older version of glibc that does not include `GLIBC_2.35`. 

### 1. **Check Amazon Linux Version**

First, determine if you're using Amazon Linux 2 or Amazon Linux 2023:

- **Amazon Linux 2**: Based on CentOS/RHEL 7, and it typically uses an older version of `glibc`.
- **Amazon Linux 2023**: A newer distribution that might have a more recent version of `glibc`.

### 2. **Use Amazon Linux 2023**
If you're using Amazon Linux 2, consider upgrading to Amazon Linux 2023, which might have the required `glibc` version.

1. Launch an Amazon Linux 2023 instance:

   ```bash
   aws ec2 run-instances --image-id ami-0abcdef1234567890 --instance-type t2.micro
   ```

2. Check the `glibc` version on Amazon Linux 2023:

   ```bash
   ldd --version
   ```

If it includes `GLIBC_2.35`, then you can use this version for your application.

### 3. **Build and Use a Custom glibc**
If upgrading to Amazon Linux 2023 is not an option, you can try building and using a custom version of `glibc`. This approach is complex and not recommended unless necessary.

1. **Download and Extract glibc**:

   ```bash
   wget http://ftp.gnu.org/gnu/libc/glibc-2.35.tar.gz
   tar -xzf glibc-2.35.tar.gz
   cd glibc-2.35
   ```

2. **Compile glibc**:

   ```bash
   mkdir build
   cd build
   ../configure --prefix=/opt/glibc-2.35
   make -j$(nproc)
   sudo make install
   ```

3. **Use the New glibc Version**:

   - Before running the application, set the `LD_LIBRARY_PATH` to include the new glibc path:

     ```bash
     export LD_LIBRARY_PATH=/opt/glibc-2.35/lib:$LD_LIBRARY_PATH
     ```

### 4. **Use Docker**
If building glibc is not feasible, consider using a Docker container that includes the required glibc version:

1. Install Docker on Amazon Linux:

   ```bash
   sudo yum install -y docker
   sudo service docker start
   sudo usermod -a -G docker ec2-user
   ```

2. Run a Docker container with a more recent version of glibc:

   ```bash
   docker run -it --rm ubuntu:22.04 /bin/bash
   ```

   Inside the container, you can run your application as Ubuntu 22.04 has `GLIBC_2.35`.

### 5. **Compile Your Application with Static Linking**
If your application is custom-built, consider compiling it with static linking to include all necessary libraries within the binary. This approach avoids dependency on the system's glibc version.

```bash
gcc -static -o myapp myapp.c
```

### Conclusion
The most practical solutions are either upgrading to Amazon Linux 2023, using Docker, or statically linking your application. Modifying glibc on a production system is risky and should be avoided if possible.
