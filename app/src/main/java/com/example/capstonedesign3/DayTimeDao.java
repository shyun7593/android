package com.example.capstonedesign3;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DayTimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DayTime dayTime);

    @Delete
    void delete(DayTime dayTime);

    @Update
    void update(DayTime dayTime);

    @Query("SELECT * FROM DayTime")
    List<DayTime> getDayTimeAll();

    @Query("SELECT * FROM DayTime WHERE day=:day")
    List<DayTime> getDay(String day);
}
