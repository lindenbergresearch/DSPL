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
import jdk.internal.joptsimple.internal.Messages.message


/**
 * Log-level base class.
 *
 * @param id    Log-level internal ID
 * @param label Textual representation
 */
sealed class LogLevel(val id: Int, label: String) extends Ordered[LogLevel] {
    
    /**
     * Implement Ordered to be able to compare LogLevels
     *
     * @param that The other Log-level
     * @return
     */
    override def compare(that: LogLevel): Int = this.id - that.id
    
    /**
     * Textual representation
     *
     * @return
     */
    override def toString: String = s"${label.toUpperCase}"
}

/**
 * Log-level object defining helper methods and default levels.
 *
 */
object LogLevel {
    var id = 0
    
    /**
     * Create a log-level with a given id and label.
     *
     * @param id    Log-level internal ID
     * @param label Textual representation
     * @return
     */
    def apply(id: Int, label: String): LogLevel = new LogLevel(id, label)
    
    /**
     * Create a log-level with a given id and label as pair.
     *
     * @param pair Pair of Int and String
     * @return
     */
    def apply(pair: (Int, String)): LogLevel = new LogLevel(pair._1, pair._2)
    
    /**
     * Create a log-level with a given label and automatic creation of the internal id.
     *
     * @param label Textual representation
     * @return
     */
    def apply(label: String): LogLevel = {
        id += 1
        LogLevel(id -> label)
    }
    
    /** Standard log-level */
    val Fatal: LogLevel = LogLevel("Fatal")
    val Error: LogLevel = LogLevel("Error")
    val Warn: LogLevel = LogLevel("Warn ")
    val Info: LogLevel = LogLevel("Info ")
    val Debug: LogLevel = LogLevel("Debug")
    val Trace: LogLevel = LogLevel("Trace")
}


/**
 * Encapsulates a created log message
 *
 * @param level     Log level
 * @param trace     Complete stack-trace
 * @param text      The actual message
 * @param exception The raised exception (on error only)
 */
sealed case class LogMessage(level: LogLevel, trace: StackTrace, text: String, exception: Option[Exception])

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
    
    /** Standard logger methods */
    def fatal(text: String, exception: Option[Exception] = None): Unit = createMessage(Fatal, text, exception)
    def warn(text: String, exception: Option[Exception] = None): Unit = createMessage(Warn, text, exception)
    def info(text: String, exception: Option[Exception] = None): Unit = createMessage(Info, text, exception)
    def debug(text: String, exception: Option[Exception] = None): Unit = createMessage(Debug, text, exception)
    def trace(text: String, exception: Option[Exception] = None): Unit = createMessage(Trace, text, exception)
    def error(text: String, exception: Option[Exception] = None): Unit = createMessage(Error, text, exception)
    
    
    info("Logger created.")
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
     * @param message Message instance
     * @return
     */
    def format(message: LogMessage, extended: Boolean = false): String = message.level match {
        case Fatal | Error | Trace => s"""[${dateToStr()}] ${message.level} ${message.text} (from: ${getStackTrace(11)})"""
        case _ => s"""[${dateToStr()}] ${message.level} ${message.text}"""
    }
    
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
    override def handle(message: LogMessage): Unit = message.level match {
        case Fatal | Error => System.err.println(format(message, extended = true))
        case _ => System.out.println(format(message))
    }
    
}



