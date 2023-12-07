# Auth-Protocols Project

## Overview
Auth-Protocols is project designed to showcase various authentication protocols. 

### Modules
- **auth-protocols-frontend**: A React-based front-end application.
- **auth-protocols-backend**: A Spring Boot REST API that forms the backend.
- **keycloak**: Contains initial realms for OIDC and SAML2 using Keycloak for identity and access management.
- **openldap**: Includes initial LDIF for OpenLDAP with a predefined user.
- **kerberos**: A Docker setup for an MIT Kerberos server, including configuration and initialization scripts for service and user principals, and a generated keytab for the service principal shared via Docker volume with the backend.

### Authentication Protocols
1. **OIDC**: Implemented using PKCE, with user self-registration enabled.
2. **SAML2**: Uses Artifact Binding, integrated through Keycloak.
3. **LDAP**: Basic LDAP authentication.
4. **Kerberos**: SPNEGO-based Kerberos authentication.

## How to start the project

### Hosts File Modification
Modify your hosts file to simulate domain names for Docker containers:
```
127.0.0.1 frontend backend keycloak openldap kerberos
```

### Kerberos Authentication Setup (skip if you not interested in kerberos authentication)
For Kerberos authentication, additional client-side setup is required:
1. **Install MIT Kerberos Client** on your Windows host ([Download MIT Kerberos](https://web.mit.edu/kerberos/dist/)).
2. **Configure `C:\ProgramData\MIT\Kerberos5\krb5.ini`** for the MIT Kerberos client:
```
[libdefaults]
 default_realm = AUTH-PROTOCOLS.COM

[realms]
 AUTH-PROTOCOLS.COM = {
   kdc = kerberos
   admin_server = kerberos
 }

[domain_realm]
 .auth-protocols.com = AUTH-PROTOCOLS.COM
 auth-protocols.com = AUTH-PROTOCOLS.COM
```

3. **Set `KRB5CCNAME` environment variable** four your ticket cache, for example:
```
KRB5CCNAME=FILE:%TEMP%\krb5cache
```
This step may require restarting your computer.

4. **Configure `.env` variables**: Set `KERBEROS_SPN=HTTP/{your_hostname}@AUTH-PROTOCOLS.COM`.
5. **Configure Firefox for MIT Kerberos**: Follow the provided screenshots in `config:about` for detailed setup instructions.
![firefox-auth-config](/kerberos/assets/firefox-auth-config.png)
![firefox-negotiate-config](/kerberos/assets/firefox-negotiate-config.png)

With this configuration Kerberos authentication will work on Firefox browser.

## Running the Application
The application uses Docker Compose for deployment.

### Prerequisites
- Docker and Docker Compose installed.

### Docker Compose Commands
Run the following commands in the root directory of the project:
- **Build Docker images**: `docker-compose build`.
- **Start services**: `docker-compose up`

## Accessing the Application
After starting services with Docker Compose, you can access the application at `localhost` in your web browser.

---

*Note: Adjust paths, environment variables, and configurations as needed based on your specific setup and requirements.*