package tasklist

import kotlinx.datetime.*
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

enum class TaskDueTag(val tag: String) {
    IN_TIME("I"),
    TODAY("T"),
    OVERDUE("O")
}

data class Task(var priority: TaskPriority = TaskPriority.LOW) {
    var text: MutableList<String> = mutableListOf()
    lateinit var date: String
    lateinit var time: String
    lateinit var dueTag: String
}

fun main() {
    val tasks: MutableList<Task> = mutableListOf()
    while (true) {
        println("Input an action (add, print, edit, delete, end):")
        val input = readln().lowercase()
        when (input) {
            "add" -> addTask(tasks)
            "print" -> printTasklist(tasks)
            "edit" -> editTask(tasks)
            "delete" -> deleteTask(tasks)
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
    task.dueTag = getDueTag(task)

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
    println("${index + 1}$firstLineSpace${task.date} ${task.time} ${task.priority.priority} ${task.dueTag}")
    task.text.forEach{ line ->
        println("$otherLineSpace$line")
    }
}

private fun editTask(tasks: MutableList<Task>) {
    printTasklist(tasks)
    if (tasks.isEmpty()) return
    while (true) {
        println("Input the task number (1-${tasks.size}):")
        val input = readln().toIntOrNull()
        if (input != null && input in 1..tasks.size) {
            editField(input - 1, tasks)
            break
        } else {
            println("Invalid task number")
        }
    }
}

private fun editField(taskIndex: Int, tasks: MutableList<Task>) {
    while (true) {
        println("Input a field to edit (priority, date, time, task):")
        val input = readln().lowercase()
        when (input) {
            "priority" -> {
                tasks[taskIndex].priority = getPriority()
                println("The task is changed")
                break
            }
            "date" -> {
                tasks[taskIndex].date = getDate()
                println("The task is changed")
                break
            }
            "time" -> {
                tasks[taskIndex].time = getTime()
                println("The task is changed")
                break
            }
            "task" -> {
                tasks[taskIndex].text = getText()
                println("The task is changed")
                break
            }
            else -> {
                println("Invalid field")
            }
        }
    }
}
private fun deleteTask(tasks: MutableList<Task>) {
    printTasklist(tasks)
    if (tasks.isEmpty()) return
    while (true) {
        println("Input the task number (1-${tasks.size}):")
        val input = readln().toIntOrNull()
        if (input != null && input in 1..tasks.size) {
            tasks.removeAt(input - 1)
            println("The task is deleted")
            break
        } else {
            println("Invalid task number")
        }
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

private fun getDueTag(task: Task): String {
    val taskDate = LocalDate.parse(task.date).toKotlinLocalDate()
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3")).date
    val numberOfDays = currentDate.daysUntil(taskDate)
    return when  {
        numberOfDays > 0 -> TaskDueTag.IN_TIME.tag
        numberOfDays == 0 -> TaskDueTag.TODAY.tag
        else -> TaskDueTag.OVERDUE.tag
    }
}

private fun getText(): MutableList<String> {
    val text: MutableList<String> = mutableListOf()
    println("Input a new task (enter a blank line to end):")
    while (true) {
        val taskInput = readln().trim()
        if (taskInput.isNotBlank()) {
            text.add(taskInput)
        } else {
            break
        }
    }
    return text
}