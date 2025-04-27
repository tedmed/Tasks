package com.example.tasks.consts

object Routes {
    const val TODO_LIST = "todoList"
    const val ADD_TODO = "addTodo"
    const val TODO_DETAIL = "todoDetail/{todoId}"
    const val TODO_EDIT = "todoDetail/{todoId}/edit"
    const val DONE_LIST = "doneList"
    const val SETTINGS = "settings"

    //Funkce pro vytvoření routy s id
    fun todoDetail(todoId: Int): String {
        return "todoDetail/$todoId"
    }

    //Funkce pro vytvoření routy s id
    fun todoEdit(todoId: Int): String {
        return "todoDetail/$todoId/edit"
    }
}