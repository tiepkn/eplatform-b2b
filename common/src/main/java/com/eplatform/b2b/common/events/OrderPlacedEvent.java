package com.eplatform.b2b.common.events;

import com.eplatform.b2b.common.dto.ReserveItemDto;
import java.util.List;

public record OrderPlacedEvent(
    String orderId,
    List<ReserveItemDto> items,
    long totalAmountCents,
    String currency
) {}
