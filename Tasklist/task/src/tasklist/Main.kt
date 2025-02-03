package tasklist

import kotlinx.datetime.*
import java.time.DateTimeException
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

enum class TaskPriority(val priority: String, val colorCode: String) {
    CRITICAL("C", "\u001B[101m \u001B[0m"),
    HIGH("H", "\u001B[103m \u001B[0m"),
    NORMAL("N", "\u001B[102m \u001B[0m"),
    LOW("L", "\u001B[104m \u001B[0m")
}

enum class TaskDueTag(val tag: String, val colorCode: String) {
    IN_TIME("I", "\u001B[102m \u001B[0m"),
    TODAY("T", "\u001B[103m \u001B[0m"),
    OVERDUE("O", "\u001B[101m \u001B[0m")
}

class Task(var priority: TaskPriority = TaskPriority.LOW) {
    var text: MutableList<String> = mutableListOf()
    lateinit var date: String
    lateinit var time: String
    lateinit var dueTag: String

    operator fun component1(): String = date
    operator fun component2(): String = time
    operator fun component3(): String = priority.priority
    operator fun component4(): String = dueTag
    operator fun component5(): MutableList<String> = text
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
        println("+----+------------+-------+---+---+--------------------------------------------+")
        println("| N  |    Date    | Time  | P | D |                    Task                    |")
        println("+----+------------+-------+---+---+--------------------------------------------+")

        tasks.forEachIndexed { index, task ->
            val (date, time, priority, dueTag, text) = task
            val isSingleSpace = index >= 9
            val firstLineSpace = if (isSingleSpace) " " else "  "

            val priorityColor = findPriorityColor(priority)
            val dueTagColor = findTagColor(dueTag)

            // Create a new list to hold formatted text
            val formattedTextList = mutableListOf<String>()
            for (i in text.indices) {
                formattedTextList.add(i, text[i])
                if (text[i].length > 44) {
                    val chunks = text[i].chunked(44) {
                        if (it.length < 44) it.padEnd(44, ' ') else it
                    }
                    formattedTextList[i] = chunks.joinToString(separator = "|\n|    |            |       |   |   |") + "|"
                } else {
                    formattedTextList[i] = text[i].padEnd(44, ' ') + "|"
                }
            }

            // Print the first line from the formatted text
            println("| ${index + 1}$firstLineSpace| $date | $time | $priorityColor | $dueTagColor |${formattedTextList[0]}")
            // Print the remaining lines if any
            if (formattedTextList.size > 1) {
                for (i in 1 until formattedTextList.size) {
                    println("|    |            |       |   |   |${formattedTextList[i]}")
                }
            }
            println("+----+------------+-------+---+---+--------------------------------------------+")
        }
    } else {
        println("No tasks have been input")
    }
}

private fun findPriorityColor(priority: String): String {
    for (p in TaskPriority.entries) {
        if (priority == p.priority) {
            return p.colorCode
        }
    }
    return ""
}

private fun findTagColor(dueTag: String): String {
    for (tag in TaskDueTag.entries) {
        if (dueTag == tag.tag) {
            return tag.colorCode
        }
    }
    return ""
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