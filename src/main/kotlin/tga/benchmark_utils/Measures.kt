package tga.benchmark_utils

import tga.text_table.printTable

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
        val table = Array<Array<Any>>(5){ emptyArray() }
        table[0] = arrayOf("'$name' ($units)", "min", "avg", "max")

        var i = 0
        for (statName in names) {
            val stat = measures[statName]!!
            val mins = stat.min().toOutput(units)
            val maxs = stat.max().toOutput(units)
            val avgs = stat.average().toLong().toOutput(units)
            table[++i] = arrayOf(statName, mins, avgs, maxs)
        }
        printTable(table){ it.joinToString() }
    }

    fun printPercentiles(units: PrintUnits){
        val table = Array<Array<Any>>(5){ emptyArray() }
        table[0] = arrayOf("'$name' percentiles ($units) ", "100%", "90%", "80%", "70%", "60%", "50%", "40%", "30%", "20%", "10%")
        var l = 0


        for (statName in names) {
            val stat = measures[statName]!!
            val percentiles = percentilesForStat(stat)
            table[++l] = Array<Any>(11){
                when(it) {
                    0 -> statName
                    else -> percentiles[it-1].toOutput(units)
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
            PrintUnits.NANO_SECONDS  -> toNanos()
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