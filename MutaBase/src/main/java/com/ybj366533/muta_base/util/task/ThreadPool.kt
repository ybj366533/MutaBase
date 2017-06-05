package com.ybj366533.muta_base.util.task

import android.os.Build

import java.io.File
import java.io.FileFilter
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

/**
 * 线程池
 * Created by wittytutu on 17-4-19.
 */

object ThreadPool {
    @Volatile private lateinit var fixedPool: ThreadPoolExecutor
    @Volatile private lateinit var cachedPool: ThreadPoolExecutor
    @Volatile private lateinit var scheduledPool: ThreadPoolExecutor
    @Volatile private lateinit var singlePool: ThreadPoolExecutor

    fun fixedThread(): ThreadPoolExecutor {
        if (fixedPool == null) {
            synchronized(ThreadPool::class.java) {
                if (fixedPool == null) {
                    var core = numberOfCPUCores
                    core = if (core > 5) core / 2 else core
                    fixedPool = Executors.newFixedThreadPool(core) as ThreadPoolExecutor
                }
            }
        }
        return fixedPool
    }

    fun cachedThread(): ThreadPoolExecutor {
        if (cachedPool == null) {
            synchronized(ThreadPool::class.java) {
                if (cachedPool == null) {
                    cachedPool = Executors.newCachedThreadPool() as ThreadPoolExecutor
                }
            }
        }
        return cachedPool
    }

    fun scheduledThread(): ThreadPoolExecutor {
        if (scheduledPool == null) {
            synchronized(ThreadPool::class.java) {
                if (scheduledPool == null) {
                    var core = numberOfCPUCores
                    core = if (core > 5) core / 2 else core
                    scheduledPool = Executors.newScheduledThreadPool(core) as ThreadPoolExecutor
                }
            }
        }
        return scheduledPool
    }

    fun singleThread(): ThreadPoolExecutor {
        if (singlePool == null) {
            synchronized(ThreadPool::class.java) {
                if (singlePool == null) {
                    singlePool = Executors.newSingleThreadExecutor() as ThreadPoolExecutor
                }
            }
        }
        return singlePool
    }

    /**
     * 获取CPU核心数

     * @return
     */
    // Gingerbread doesn't support giving a single application access to both cores, but a
    // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
    // chipset and Gingerbread; that can let an app in the background run without impacting
    // the foreground application. But for our purposes, it makes them single core.
    val numberOfCPUCores: Int
        get() {
            val defNumber = 2
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                return 2
            }
            var cores = defNumber
            try {
                cores = File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).size
                cores = if (cores < defNumber) defNumber else cores
            } catch (e: Exception) {
            }

            return cores
        }

    private val CPU_FILTER = FileFilter { pathname ->
        val path = pathname.name
        //regex is slow, so checking char by char.
        if (path.startsWith("cpu")) {
            for (i in 3..path.length - 1) {
                if (path[i] < '0' || path[i] > '9') {
                    return@FileFilter false
                }
            }
            return@FileFilter true
        }
        false
    }

}
