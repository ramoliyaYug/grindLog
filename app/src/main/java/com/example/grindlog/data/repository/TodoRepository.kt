package com.example.grindlog.data.repository

import com.example.grindlog.data.local.dao.TodoDao
import com.example.grindlog.data.local.entity.Todo
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getTodosByDate(date: Date): Flow<List<Todo>> = todoDao.getTodosByDate(date)

    fun getAllTodos(): Flow<List<Todo>> = todoDao.getAllTodos()

    fun getTodosBetweenDates(startDate: Date, endDate: Date): Flow<List<Todo>> =
        todoDao.getTodosBetweenDates(startDate, endDate)

    suspend fun getTodoById(id: Long): Todo? = todoDao.getTodoById(id)

    suspend fun insertTodo(todo: Todo): Long = todoDao.insertTodo(todo)

    suspend fun updateTodo(todo: Todo) = todoDao.updateTodo(todo)

    suspend fun deleteTodo(todo: Todo) = todoDao.deleteTodo(todo)

    suspend fun deleteAllTodos() = todoDao.deleteAllTodos()
}
