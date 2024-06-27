The `dotnet dev-certs` tool does not support creating certificates without an expiration date directly. The certificates created using `dotnet dev-certs` have a default expiration period of one year.

If you need a self-signed certificate with a custom expiration period (including no expiration), you can use `openssl` or `PowerShell` instead. Here's how you can do it using `openssl`:

### Using OpenSSL to Create a Self-Signed Certificate with No Expiration

1. **Generate a private key:**
   ```bash
   openssl genpkey -algorithm RSA -out key.pem
   ```

2. **Generate a certificate signing request (CSR):**
   ```bash
   openssl req -new -key key.pem -out csr.pem
   ```

3. **Generate the self-signed certificate with a custom expiration period (e.g., 100 years):**
   ```bash
   openssl x509 -req -days 36500 -in csr.pem -signkey key.pem -out cert.pem
   ```

4. **Convert the certificate and key to .pfx format:**
   ```bash
   openssl pkcs12 -export -out app.pfx -inkey key.pem -in cert.pem -password pass:your-password
   ```

### Example Steps in Detail

1. **Generate a private key:**
   ```bash
   openssl genpkey -algorithm RSA -out key.pem
   ```

2. **Generate a certificate signing request (CSR):**
   ```bash
   openssl req -new -key key.pem -out csr.pem
   ```

   You will be prompted to enter information such as country name, state, organization, etc.

3. **Generate the self-signed certificate with a long expiration period:**
   ```bash
   openssl x509 -req -days 36500 -in csr.pem -signkey key.pem -out cert.pem
   ```

4. **Convert the certificate and key to .pfx format:**
   ```bash
   openssl pkcs12 -export -out app.pfx -inkey key.pem -in cert.pem -password pass:your-password
   ```

This command will generate a `.pfx` file named `app.pfx` with a password of `your-password` in the current directory. The certificate will be valid for approximately 100 years (36500 days).

### Storing the Certificate in EFS and Mounting to ECS

Follow the same steps as mentioned earlier to store the certificate in EFS and mount it to an ECS task.
