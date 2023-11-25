# Use a base image with Kerberos installed
FROM centos:7

# Install Kerberos server
RUN yum -y install krb5-server krb5-libs krb5-workstation

# Add Kerberos configuration files
COPY krb5.conf /etc/krb5.conf
COPY kdc.conf /var/kerberos/krb5kdc/kdc.conf
COPY kadm5.acl /var/kerberos/krb5kdc/kadm5.acl

# Create Kerberos database
RUN kdb5_util create -s -P password

# Expose Kerberos ports
EXPOSE 88 749

# Start Kerberos
CMD ["krb5kdc", "-n"]