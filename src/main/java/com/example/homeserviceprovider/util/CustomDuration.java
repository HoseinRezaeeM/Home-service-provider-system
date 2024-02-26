package com.example.homeserviceprovider.util;


import com.example.homeserviceprovider.domain.offer.Offer;
import com.example.homeserviceprovider.domain.order.Order;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class CustomDuration {

    public String getDuration(LocalDateTime startTime, LocalDateTime endTime, String type) {
        Duration duration = Duration.between(startTime, endTime);
        StringBuilder stringBuilder = new StringBuilder();
        if (type.equals(Order.class.getName()))
            stringBuilder.append("Requested duration recorded time to do the subServices: ");
        else if (type.equals(Offer.class.getName()))
            stringBuilder.append("Suggested duration recorded time to do the subServices: ");
        if (duration.toDaysPart() > 0) {
            stringBuilder.append(duration.toDaysPart());
            if (duration.toDaysPart() > 1)
                stringBuilder.append(" Days : ");
            else stringBuilder.append(" Day : ");
        }
        if (duration.toHoursPart() > 0) {
            stringBuilder.append(duration.toHoursPart());
            if (duration.toHoursPart() > 1)
                stringBuilder.append(" Hours : ");
            else stringBuilder.append(" Hour : ");
        }
        if (duration.toMinutesPart() > 0) {
            stringBuilder.append(duration.toMinutesPart());
            if (duration.toMinutesPart() > 1)
                stringBuilder.append(" Minutes ");
            else stringBuilder.append(" Minute ");
        }
        return stringBuilder.toString();
    }


}
