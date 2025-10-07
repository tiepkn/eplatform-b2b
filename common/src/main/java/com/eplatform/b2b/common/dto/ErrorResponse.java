package com.eplatform.b2b.common.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(String code, String message, OffsetDateTime timestamp, String path) {}
