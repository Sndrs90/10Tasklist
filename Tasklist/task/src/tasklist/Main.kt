package tasklist

fun main() {
    val tasks: MutableList<MutableList<String>> = mutableListOf()
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

private fun addTask(tasks: MutableList<MutableList<String>>) {
    println("Input a new task (enter a blank line to end):")
    val task = mutableListOf<String>()
    while (true) {
        val taskInput = readln().trim()
        if (taskInput.isNotBlank()) {
            task.add(taskInput)
        } else {
            if (task.isNotEmpty()) {
                tasks.add(task)
            } else {
                println("The task is blank")
            }
            break
        }
    }
}

private fun printTasklist(tasks: MutableList<MutableList<String>>) {
    if (tasks.isNotEmpty()) {
        tasks.forEachIndexed { index, task ->
            printTask(index, task)
            println()
        }
    } else {
        println("No tasks have been input")
    }
}

private fun printTask(index: Int, task: MutableList<String>) {
    // Determine if we should use single space or double space for the first line
    val isSingleSpace = index >= 9
    val firstLineSpace = if (isSingleSpace) " " else "  "
    val otherLineSpace = if (isSingleSpace) " " else "   "

    task.forEachIndexed { i, line ->
        if (i == 0) {
            // Print first line
            println("${index + 1}$firstLineSpace$line")
        } else {
            // Print subsequent lines
            println("$otherLineSpace$line")
        }
    }
}