package com.example.grindlog.data.local.dao

import androidx.room.*
import com.example.grindlog.data.local.entity.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE date = :date ORDER BY createdAt DESC")
    fun getTodosByDate(date: Date): Flow<List<Todo>>

    @Query("SELECT * FROM todos ORDER BY date DESC, createdAt DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTodosBetweenDates(startDate: Date, endDate: Date): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): Todo?

    @Insert
    suspend fun insertTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()

    @Query("SELECT * FROM todos WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTodosInDateRange(startDate: Date, endDate: Date): List<Todo>
}
