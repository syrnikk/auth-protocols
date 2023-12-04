#!/bin/bash

FLAG_FILE="/etc/krb5/init/.initialized"

if [ ! -f "$FLAG_FILE" ]; then
    # Initialize principals and keytabs
    kadmin.local -q "addprinc -pw password johndoe-kerberos@AUTH-PROTOCOLS.COM"
    kadmin.local -q "addprinc -randkey ${KERBEROS_SPN}"
    kadmin.local -q "ktadd -k /etc/krb5/keytabs/krb5.keytab ${KERBEROS_SPN}"

    # Create a flag file to indicate completion
    touch "$FLAG_FILE"
fi
