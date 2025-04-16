package com.example.tasks.consts

object Routes {
    const val TodoList = "todoList"
    const val AddTodo = "addTodo"
    const val TodoDetail = "todoDetail/{todoId}"
    const val TodoEdit = "todoDetail/{todoId}/edit"
    const val DoneList = "doneList"
    const val Settings = "settings"

    //Funkce pro vytvoření routy s id
    fun TodoDetail(todoId: Int): String {
        return "todoDetail/$todoId"
    }

    //Funkce pro vytvoření routy s id
    fun TodoEdit(todoId: Int): String {
        return "todoDetail/$todoId/edit"
    }
}