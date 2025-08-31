package com.workitech.s19.challenge.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeAgoUtil {

    public static String toRelative(LocalDateTime time) {
        Duration duration = Duration.between(time, LocalDateTime.now());

        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) return "şimdi";
        else if (minutes < 60) return minutes + " dk önce";
        else if (hours < 24) return hours + " saat önce";
        else return days + " gün önce";
    }
}