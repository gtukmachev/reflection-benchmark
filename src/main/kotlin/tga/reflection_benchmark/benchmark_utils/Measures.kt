package tga.reflection_benchmark.benchmark_utils

import tga.reflection_benchmark.text_table.printTable

class Measures(var name: String, capacity: Int, val names: Array<String>) {
    private val measures: HashMap<String, ArrayList<Long>> = HashMap()

    init {
        names.forEach {
            measures[it] = ArrayList(capacity)
        }
    }

    fun <T> track(name: String, body: () -> T ): T {
        val startedAt = System.nanoTime()
        val result: T = body()
        val finishedAt = System.nanoTime()
        measures[name]!!.add(finishedAt - startedAt)
        return result
    }

    fun printStatistic(units: PrintUnits) {
        printAvgStatistic(units)
        println("")
        printPercentiles(units)
    }

    fun printAvgStatistic(units: PrintUnits){
        val table = Array<Array<Any>>(names.size+2){ emptyArray() }
        var i = -1
        table[++i] = arrayOf("'$name' ($units)", "", "min", "avg", "max")
        table[++i] = Array(5){"."}

        for (statName in names) {
            val stat = measures[statName]!!
            val mins = stat.min().toOutput(units)
            val maxs = stat.max().toOutput(units)
            val avgs = stat.average().toLong().toOutput(units)
            table[++i] = arrayOf(statName, ":", mins, avgs, maxs)
        }
        printTable(table){ it.joinToString("  ") }
    }

    fun printPercentiles(units: PrintUnits){
        val table = Array<Array<Any>>(names.size+2){ emptyArray() }
        var l = -1

        table[++l] = arrayOf("'$name' percentiles ($units) ", "", "100%", "90%", "80%", "70%", "60%", "50%", "40%", "30%", "20%", "10%")
        table[++l] = Array(12){"."}


        for (statName in names) {
            val stat = measures[statName]!!
            val percentiles = percentilesForStat(stat)
            table[++l] = Array<Any>(12){
                when(it) {
                    0 -> statName
                    1 -> ":"
                    else -> percentiles[it-2].toOutput(units)
                }
            }
        }
        printTable(table){ it.joinToString("  ") }
    }

    private fun percentilesForStat(stat: java.util.ArrayList<Long>): Array<Long> {
        val sortedStat = stat.sorted()
        val size = stat.size

        val result = Array(10){
            val p = (it+1) * 10

            val i = if (p == 100) {
                size-1
            } else {
                val prc = p.toDouble() / 100
                (stat.size * prc).toInt() - 1
            }

            sortedStat[i]
        }

        result.reverse()

        return result
    }

    private fun Long.toOutput(units: PrintUnits): String {
        return when (units) {
            PrintUnits.NANO_SECONDS -> toNanos()
            PrintUnits.MILLI_SECONDS -> toMillis()
        }
    }

    private fun Long.toMillis(): String {
        val d = this.toDouble()
        val r = d / 1000000.0
        return String.format("%1\$,.2f", r) + "ms"
    }

    private fun Long.toNanos(): String {
        val n = this
        return n.toString()
    }
}