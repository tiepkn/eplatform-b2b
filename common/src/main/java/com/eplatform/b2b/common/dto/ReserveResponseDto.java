package com.eplatform.b2b.common.dto;

import java.util.List;

public record ReserveResponseDto(
    boolean success,
    List<String> failedSkus
) {}
