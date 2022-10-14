package tga.reflection_benchmark

import tga.benchmark_utils.Measures
import tga.benchmark_utils.PrintUnits.NANO_SECONDS
import java.lang.reflect.Method

const val CONSTRUCTOR_CALL      = "         constructor"
const val ATTR_SET              = "             setName"
const val ATTR_REFLECTION       = "(ref)       setPhone"
const val ATTR_REFLECTION_CACHE = "(ref + cache) setDay"



fun main(args: Array<String>) {
    // val wormAuSessions = 10_000
    val numberOfSessions = 10000000


    val kotlinDirectStat = Measures("Kotlin direct", numberOfSessions,
        CONSTRUCTOR_CALL,
        ATTR_SET,
        ATTR_REFLECTION,
        ATTR_REFLECTION_CACHE
    )

    val mapSetters = HashMap<Class<*>, Method>()

    for (i in 0 until numberOfSessions) {
        val id = i.toLong()

        val obj = kotlinDirectStat.track(CONSTRUCTOR_CALL) {
            newPersonOfRandomClass()
        }

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

    }

    kotlinDirectStat.printStatistic(NANO_SECONDS)
}
