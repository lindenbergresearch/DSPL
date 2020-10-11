/*                                                                     *\
**       __   ___  ______                     __         __            **
**      / /  / _ \/_  __/                 ___/ /__ ___  / /            **
**     / /__/ , _/ / /    Lindenberg     / _  (_-</ _ \/ /__           **
**    /____/_/|_| /_/  Research Tec.     \_,_/___/ .__/____/           **
**                                              /_/                    **
**                                                                     **
**                                                                     **
**	  https://github.com/lindenbergresearch/LRTRack	                   **
**    heapdump@icloud.com                                              **
**		                                                               **
**    Digital Signal Programming Language                              **
**    Copyright 2017-2020 by Patrick Lindenberg / LRT                  **
**                                                                     **
**    For Redistribution and use in source and binary forms,           **
**    with or without modification please see LICENSE.                 **
**                                                                     **
\*                                                                     */

package com.heapdump.dspl.utils

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.heapdump.dspl.utils.LogLevel._
import com.heapdump.dspl.utils.Utils.{StackTrace, getStackTrace}


/**
 * Define a log level
 *
 * @param order Order ID
 */
case class LogLevel(order: Int) extends Ordered[LogLevel] {
    /**
     * Implement to provide comparison of log-levels
     * @param that The other log-level
     * @return
     */
    override def compare(that: LogLevel): Int = this.order - that.order
    
    /**
     * Returns a textual representation of a log-level
     *
     * @return
     */
    override def toString: String = order match {
        case 0x00 => "FATAL"
        case 0x02 => "ERROR"
        case 0x03 => "WARN"
        case 0x04 => "INFO"
        case 0x05 => "DEBUG"
        case 0x06 => "TRACE"
    }
}


/**
 * All used log-levels
 */
object LogLevel {
    val Fatal: LogLevel = LogLevel(0x00)
    val Error: LogLevel = LogLevel(0x02)
    val Warn: LogLevel = LogLevel(0x03)
    val Info: LogLevel = LogLevel(0x04)
    val Debug: LogLevel = LogLevel(0x05)
    val Trace: LogLevel = LogLevel(0x06)
}


/**
 * Encapsulates a created log message
 *
 * @param level     Log level
 * @param trace     Complete stack-trace
 * @param text      The actual message
 * @param exception The raised exception (on error only)
 */
case class LogMessage(level: LogLevel, trace: StackTrace, text: String, exception: Option[Exception]) {

}


/**
 * Simple logging object
 */
object Logger {
    /**
     * Config class
     *
     * @param level    Current log-level
     * @param useCache Determines if messages get cached
     */
    sealed case class Config(level: LogLevel, useCache: Boolean, var handler: List[AbstractLogHandler])
    
    /** default config */
    private var config: Config = Config(Debug, useCache = false, List(new ConsoleLogHandler, new DummyLogHandler))
    def setupLogger: Config = config
    def setupLogger_=(newConfig: Config): Unit = config = newConfig
    
    /** message cache */
    private val cache = new util.ArrayList[LogMessage]
    
    
    /**
     * Internal creation of a log message instance.
     *
     * @param level     The log-level
     * @param text      The textual message
     * @param exception Optional exception
     * @return
     */
    private def createMessage(level: LogLevel, text: String, exception: Option[Exception]): Unit = {
        val message = LogMessage(level, getStackTrace, text, exception)
        
        if (level <= config.level) {
            // push log to handler
            if (config.useCache) cache.add(message)
        }
        
        config.handler.foreach(_.handle(message))
    }
    
    /**
     * Log as debug message
     *
     * @param text
     * @param exception
     */
    def debug(text: String, exception: Option[Exception] = None): Unit = {
        createMessage(Debug, text, exception)
    }
    
}

/**
 * Simple output adapter for logging messages
 */
trait AbstractLogHandler {
    
    /**
     * Return the current date string.
     *
     * @return
     */
    private def dateToStr(format: String = "HH:mm:ss.SSS") = {
        val date = new Date()
        val DateFor = new SimpleDateFormat(format)
        DateFor.format(date)
    }
    
    /**
     * Return the log-message as formatted string
     *
     * @param message
     * @return
     */
    def format(message: LogMessage, extended: Boolean = false) = s"""[${dateToStr()}] ${message.level} ${message.text} ${if (extended) getStackTrace(4)}"""
    
    /**
     * Handler to be implemented.
     *
     * @param message The log message to be handled
     * @return
     */
    def handle(message: LogMessage): Unit = ???
}


class DummyLogHandler extends AbstractLogHandler {
    /**
     * Handler to be implemented.
     *
     * @param message The log message to be handled
     * @return
     */
    override def handle(message: LogMessage): Unit = {
    
    }
}

/**
 * Console log handler.
 */
class ConsoleLogHandler extends AbstractLogHandler {
    /**
     * Handler to be implemented.
     *
     * @param message The log message to be handled
     * @return
     */
    override def handle(message: LogMessage): Unit =
        println(format(message))
}



