@file:Suppress("unused") // for use in scripts

package me.kcra.cellulose.script.extension

import java.util.logging.Level
import java.util.logging.Logger

fun Logger.error(msg: String, throwable: Throwable? = null) = log(Level.SEVERE, msg, throwable)