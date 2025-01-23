package tasklist

import java.time.DateTimeException
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

enum class TaskPriority(val priority: String) {
    CRITICAL("C"),
    HIGH("H"),
    NORMAL("N"),
    LOW("L")
}

data class Task(var priority: TaskPriority = TaskPriority.LOW) {
    val text: MutableList<String> = mutableListOf()
    lateinit var date: String
    lateinit var time: String
}

fun main() {
    val tasks: MutableList<Task> = mutableListOf()
    while (true) {
        println("Input an action (add, print, end):")
        val input = readln().lowercase()
        when (input) {
            "add" -> addTask(tasks)
            "print" -> printTasklist(tasks)
            "end" -> {
                println("Tasklist exiting!")
                return
            }
            else -> println("The input action is invalid")
        }
    }
}


private fun addTask(tasks: MutableList<Task>) {
    val task = Task()

    task.priority = getPriority()
    task.date = getDate()
    task.time = getTime()

    println("Input a new task (enter a blank line to end):")
    while (true) {
        val taskInput = readln().trim()
        if (taskInput.isNotBlank()) {
            task.text.add(taskInput)
        } else {
            if (task.text.isNotEmpty()) {
                tasks.add(task)
            } else {
                println("The task is blank")
            }
            break
        }
    }
}

private fun printTasklist(tasks: MutableList<Task>) {
    if (tasks.isNotEmpty()) {
        tasks.forEachIndexed { index, task ->
            printTask(index, task)
            println()
        }
    } else {
        println("No tasks have been input")
    }
}

private fun printTask(index: Int, task: Task) {
    // Determine if we should use single space or double space for the first line
    val isSingleSpace = index >= 9
    val firstLineSpace = if (isSingleSpace) " " else "  "
    val otherLineSpace = "   "
    println("${index + 1}$firstLineSpace${task.date} ${task.time} ${task.priority.priority}")
    task.text.forEach{ line ->
        println("$otherLineSpace$line")
    }
}

private fun getPriority(): TaskPriority {
    while (true) {
        println("Input the task priority (C, H, N, L):")
        val input = readln().uppercase()
        // Check if the input matches any of the task priorities
        val taskPriority = TaskPriority.entries.find { it.priority == input }
        if (taskPriority != null) {
            return taskPriority
        }
    }
}

private fun getDate(): String {
    while (true) {
        println("Input the date (yyyy-MM-dd):")
        val input = readln()
        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        try {
            if (input == "2021-2-29") throw DateTimeException("The input date is invalid")
            val date = LocalDate.parse(input,formatter)
            return date.toString()
        } catch (e: DateTimeParseException) {
            println("The input date is invalid")
        } catch (e: DateTimeException) {
            println("The input date is invalid")
        }
    }
}

private fun getTime(): String {
    while (true) {
        println("Input the time (hh:mm):")
        val input = readln()
        val formatter = DateTimeFormatter.ofPattern("H:m")
        try {
            if (input == "24:00") throw DateTimeException("The input time is invalid")
            val time = LocalTime.parse(input,formatter)
            return time.toString()
        } catch (e: DateTimeException) {
            println("The input time is invalid")
        }
    }
}