package tga.reflection_benchmark

import tga.benchmark_utils.Measures
import tga.benchmark_utils.PrintUnits.NANO_SECONDS
import java.lang.reflect.Method

const val ATTR_SET              = "polymorphism (common interface)"
const val ATTR_REFLECTION       = "reflection obj::Class.getMethod + method.invoke(..)"
const val ATTR_REFLECTION_CACHE = "reflection + HashMap cache (onj::Class) -> setter method"
const val ATTR_SWITCH           = "when(obj) {is Class1 -> obj.setVal(...) ... }"



fun main() {
    val numberOfSessions = 10_000_000

    val kotlinDirectStat = Measures("Kotlin set field value", numberOfSessions, arrayOf(
        ATTR_SET,
        ATTR_REFLECTION,
        ATTR_REFLECTION_CACHE,
        ATTR_SWITCH
    ))

    val mapSetters = HashMap<Class<*>, Method>()

    for (i in 0 until numberOfSessions) {
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
