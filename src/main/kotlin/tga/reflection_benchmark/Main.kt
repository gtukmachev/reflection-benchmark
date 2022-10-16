package tga.reflection_benchmark

import tga.benchmark_utils.Measures
import tga.benchmark_utils.PrintUnits.NANO_SECONDS
import java.lang.reflect.Method

const val ATTR_SET              = "common interface    "
const val ATTR_REFLECTION       = "reflection          "
const val ATTR_REFLECTION_CACHE = "reflection + hashmap"
const val ATTR_SWITCH           = "when(obj)           "



fun main(args: Array<String>) {
    // val wormAuSessions = 10_000
    val numberOfSessions = 10000000


    val kotlinDirectStat = Measures("Kotlin direct", numberOfSessions,
        ATTR_SET,
        ATTR_REFLECTION,
        ATTR_REFLECTION_CACHE,
        ATTR_SWITCH
    )

    val mapSetters = HashMap<Class<*>, Method>()

    for (i in 0 until numberOfSessions) {
        val id = i.toLong()

        val obj = newPersonOfRandomClass()

        kotlinDirectStat.track(ATTR_SET) {
            obj.name = "123"
        }

        kotlinDirectStat.track(ATTR_REFLECTION) {
            val setter = obj::class.java.getMethod("setPhone", String::class.java)
            setter.invoke(obj, "345")
        }


        kotlinDirectStat.track(ATTR_REFLECTION_CACHE) {
            val setter = mapSetters.computeIfAbsent(obj::class.java) { objClass ->
                objClass.getMethod("setDay", String::class.java)
            }
            setter.invoke(obj, "567")
        }

        kotlinDirectStat.track(ATTR_SWITCH) {
            obj.setGoalSwitch("678")
        }

    }

    kotlinDirectStat.printStatistic(NANO_SECONDS)
}
