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

/**
 * A collection of utility methods and classes which
 * which belongs to no concrete category.
 */
object Utils {
    
    /**
     * Stack trace element containing relevant data
     *
     * @param fileName   The filename
     * @param methodName The method name
     * @param lineNumber The line number
     */
    case class TraceElement(fileName: String, methodName: String, lineNumber: Int) {
        override def toString: String = s"""<${fileName}>::${methodName}:${lineNumber}"""
    }
    
    
    type StackTrace = List[TraceElement]
    
    /**
     * Get Stacktrace elements.
     *
     * @see java.lang.StackTraceElement
     * @return StackTrace
     */
    def getStackTrace: StackTrace =
        (for (t <- Thread.currentThread.getStackTrace)
            yield TraceElement(t.getFileName, t.getMethodName, t.getLineNumber)).toList
}
