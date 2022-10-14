package tga.benchmark_utils

class Measures(var name: String, capacity: Int, vararg names: String) {
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
        printPercentiles(units)
    }

    fun printAvgStatistic(units: PrintUnits){
        println("$name ($units): min - avg - max {")
        for ( (name, stat) in measures) {
            val mins = stat.min().toOutput(units)
            val maxs = stat.max().toOutput(units)
            val avgs = stat.average().toLong().toOutput(units)
            println("\t$name: $mins - $avgs - $maxs")
        }
        println("}")
    }

    fun printPercentiles(units: PrintUnits){
        println("$name ($units): percentiles {")
        for ( (statName, stat) in measures) {
            val pcnt: Array<Pair<Int, Long>> = percentilesForStat(stat)
            val pcntStr = pcnt.map{ (percentile, percentileValue) ->
                "$percentile%: ${percentileValue.toOutput(units)}"
            }
            println("\t$statName: [${pcntStr.joinToString()}]")
        }

        println("}")

    }

    private fun percentilesForStat(stat: java.util.ArrayList<Long>): Array<Pair<Int, Long>> {
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

            p to sortedStat[i]
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
        return String.format("%1\$,.2f", r).padStart(8) + "ms"
    }

    private fun Long.toNanos(): String {
        val n = this
        return n.toString().padStart(4)    }
}