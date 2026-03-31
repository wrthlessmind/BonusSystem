package com.bonussystem.common.util;

import com.bonussystem.common.model.enums.CalculationStatus;
import com.bonussystem.common.model.enums.PeriodStatus;
import com.bonussystem.common.model.enums.Role;
import com.bonussystem.common.model.enums.UserStatus;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GsonProvider {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Gson INSTANCE = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>)
                    (src, type, ctx) -> new JsonPrimitive(src.format(FORMATTER)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                    (json, type, ctx) -> LocalDateTime.parse(json.getAsString(), FORMATTER))
            .registerTypeAdapter(Role.class, (JsonSerializer<Role>)
                    (src, type, ctx) -> new JsonPrimitive(src.getDbValue()))
            .registerTypeAdapter(Role.class, (JsonDeserializer<Role>)
                    (json, type, ctx) -> Role.fromDbValue(json.getAsString()))
            .registerTypeAdapter(UserStatus.class, (JsonSerializer<UserStatus>)
                    (src, type, ctx) -> new JsonPrimitive(src.getDbValue()))
            .registerTypeAdapter(UserStatus.class, (JsonDeserializer<UserStatus>)
                    (json, type, ctx) -> UserStatus.fromDbValue(json.getAsString()))
            .registerTypeAdapter(PeriodStatus.class, (JsonSerializer<PeriodStatus>)
                    (src, type, ctx) -> new JsonPrimitive(src.getDbValue()))
            .registerTypeAdapter(PeriodStatus.class, (JsonDeserializer<PeriodStatus>)
                    (json, type, ctx) -> PeriodStatus.fromDbValue(json.getAsString()))
            .registerTypeAdapter(CalculationStatus.class, (JsonSerializer<CalculationStatus>)
                    (src, type, ctx) -> new JsonPrimitive(src.getDbValue()))
            .registerTypeAdapter(CalculationStatus.class, (JsonDeserializer<CalculationStatus>)
                    (json, type, ctx) -> CalculationStatus.fromDbValue(json.getAsString()))
            .create();

    private GsonProvider() {}

    public static Gson get() {
        return INSTANCE;
    }
}