package com.eplatform.b2b.common.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ReserveRequestDto(
    @NotEmpty List<ReserveItemDto> items
) {}
