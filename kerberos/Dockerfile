# Use a base image with Kerberos installed
FROM centos:7

# Install Kerberos server
RUN yum -y install krb5-server krb5-libs krb5-workstation

# Add Kerberos configuration files
COPY config/krb5.conf /etc/krb5.conf
COPY config/kdc.conf /var/kerberos/krb5kdc/kdc.conf
COPY config/kadm5.acl /var/kerberos/krb5kdc/kadm5.acl

# Argument for database password
ARG KERBEROS_DB_PASS

# Create Kerberos database
RUN kdb5_util create -s -P ${KERBEROS_DB_PASS}

# Expose Kerberos ports
EXPOSE 88 749

# Start Kerberos
CMD ["krb5kdc", "-n"]