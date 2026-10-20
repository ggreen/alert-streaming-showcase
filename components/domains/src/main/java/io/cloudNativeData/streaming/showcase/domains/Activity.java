package io.cloudNativeData.streaming.showcase.domains;

import lombok.Builder;

/**
 * Activity details
 * @param id the unique
 * @param icon the icon
 * @param time the time of the activities
 * @param activity the activity notes
 */
@Builder
public record Activity(String id,
                       String account,
                       String icon,
                       String time,
                       String activity) {
}
