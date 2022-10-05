package com.example.capstonedesign3;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FinallincityDao {
    @Insert
    void insert(Finallincity finallincity);

    @Update
    void update(Finallincity finallincity);

    @Delete
    void delete(Finallincity finallincity);

    @Query("SELECT * FROM Finallincity")
    List<Finallincity> getFinallincityAll();

    @Query("DELETE FROM Finallincity WHERE Day = :day")
    void delday(String day);
}
