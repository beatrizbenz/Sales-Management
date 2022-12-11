package com.example.Gestao_De_Brigadeiros.Persistencia;

import androidx.room.TypeConverter;

import java.util.Date;

//Class para converter as datas de formato inteiro para string no formato americano <> brasileiro
public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
