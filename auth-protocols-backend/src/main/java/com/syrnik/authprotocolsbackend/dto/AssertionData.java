package com.syrnik.authprotocolsbackend.dto;

import java.util.Set;

public record AssertionData(String nameID, String sessionIndex, Set<String> authorities) {
}
