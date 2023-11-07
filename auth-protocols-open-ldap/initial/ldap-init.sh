#!/bin/bash

# Define paths to LDIF file and LDAP data directory
LDIF_FILE="/initial/ldap-init.ldif"
LDAP_DATA_DIR="/var/lib/ldap"

sleep 5

# Check if the LDIF data has already been added
if [ -f "$LDAP_DATA_DIR/ldif-data-added" ]; then
    echo "LDIF data has already been added. Skipping."
else
    # LDIF data has not been added, so proceed with initialization
    echo "Initializing LDAP data from $LDIF_FILE"
    touch "$LDAP_DATA_DIR/ldif-data-added"
    ldapadd -x -D "cn=admin,$LDAP_BASE_DN" -w "$LDAP_ADMIN_PASSWORD" -H ldap://openldap -f "$LDIF_FILE"
fi

tail -f /dev/null