package org.images;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import jakarta.persistence.Column;
import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Service
public class TimeFormatter {

    public TimeFormatter() {
    }

    public String getFormatData() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter customTime = DateTimeFormatter.ofPattern("HH:mm 'T' MM-dd-yyyy");

        return now.format(customTime);
    }

    @Override
    public String toString(){
        return getFormatData();
    }
}
