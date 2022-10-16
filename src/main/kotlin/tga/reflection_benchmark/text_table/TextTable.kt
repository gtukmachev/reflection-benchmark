package tga.reflection_benchmark.text_table

fun printTable(table: Array<Array<Any>>, joiner: (Array<String>) -> String) {
    val forPrint = toSameWidth(table)
    forPrint.forEach { line ->
        val str = joiner(line)
        println(str)
    }
}

fun toSameWidth(table: Array<Array<Any>>): Array<Array<String>>{
    val result = ArrayList<Array<String>>()

    val widthOfColumn: IntArray = calculateColumnWidths(table)

    for (l in table.indices) {
        val line = table[l]
        val lineStrings = Array(line.size){""}
        for (c in line.indices) {
            val v = table[l][c].toString()
            lineStrings[c] = v.padStart(widthOfColumn[c])
        }
        result.add(lineStrings)
    }

    return result.toTypedArray()
}

fun calculateColumnWidths(table: Array<Array<Any>>): IntArray {
    val widths = IntArray(table.first().size)

    for (c in widths.indices) {
        var maxLen = 0
        for (l in table.indices) {
            val len = table[l][c].toString().length
            if (len > maxLen) maxLen = len
        }
        widths[c] = maxLen
    }
    return widths
}

