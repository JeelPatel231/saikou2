package ani.saikou2.data.plugins

import android.content.Context
import dalvik.system.DexClassLoader
import java.io.File
import kotlin.reflect.full.primaryConstructor

inline fun <reified T>loadAndCreateInstance(ctx: Context, jarName: String, className: String) : T {
//    val absPath = File(ctx.cacheDir, jarName).absolutePath
    val absPath = File(ctx.getExternalFilesDir(null)!!.path, jarName).absolutePath
    println(absPath)
    val classLoader = DexClassLoader(
        absPath,
        null,
        null,
        ctx.classLoader
    )
    val class_ = Class.forName(className, true, classLoader).kotlin

    val instance = class_.objectInstance ?: class_.primaryConstructor!!.call()

    if (instance is T) {
        return instance
    } else {
        throw IllegalArgumentException("Class $className does not implement given class")
    }
}

inline fun <reified T>loadPluginSafe(ctx: Context, jarName: String, className: String) : T? {
    return try {
        loadAndCreateInstance<T>(ctx, jarName, className)
    } catch (e: Throwable){
        e.printStackTrace()
        null
    }
}