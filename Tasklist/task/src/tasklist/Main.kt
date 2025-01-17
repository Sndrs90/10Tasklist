package tasklist

fun main() {
    val tasks = mutableListOf<Task>()
    var index = 1
    println("Input the tasks (enter a blank line to end):")
    while(true) {
        val input = readln()
        when {
            input.isNotBlank() -> {
                val task = Task(index, input.trim())
                tasks.add(task)
                index++
            }
            input.isBlank() && tasks.isEmpty() -> {
                println("No tasks have been input")
                break
            }
            input.isBlank() -> {
                printTasklist(tasks)
                break
            }
        }
    }
}

private fun printTasklist(tasks: MutableList<Task>) = tasks.forEach { it.printTask() }

class Task(private val index: Int, private val text: String) {
    fun printTask() {
        if (index < 10) println("$index  $text") else println("$index $text")
    }
}