package it.gov.pagopa.ranking.utils.csv;

import com.opencsv.bean.AbstractBeanField;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter extends AbstractBeanField<String, LocalDateTime> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected LocalDateTime convert(String s) {
        if(StringUtils.isEmpty(s)){
            return null;
        } else {
            return LocalDateTime.parse(s, formatter);
        }
    }
}