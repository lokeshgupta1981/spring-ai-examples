package com.howtodoinjava.orderdesk.vision;

public record DamageReport(String orderNumber, String damageType, boolean labelReadable,
                           String summary) {
}
