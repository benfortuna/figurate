/**
 * This file is part of Figurate.
 *
 * Copyright (c) 2012, Ben Fortuna [fortuna@micronode.com]
 *
 * Figurate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Figurate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Figurate.  If not, see <http://www.gnu.org/licenses/>.
 */
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;

appender("CONSOLE", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
	  pattern = "[%date] %level %thread %logger - %msg%n"
	}
}

appender("FILE", RollingFileAppender) {
	file = "${System.getProperty('user.home')}/.figurate/logs/figurate.log"
	append = true
	encoder(PatternLayoutEncoder) {
	  pattern = "[%date] %level %logger - %msg%n"
	}
	rollingPolicy(FixedWindowRollingPolicy) {
		fileNamePattern = "${System.getProperty('user.home')}/.figurate/logs/figurate.log.%i"
		minIndex = 1
		maxIndex = 4
	}
	triggeringPolicy(SizeBasedTriggeringPolicy) {
		maxFileSize = '100KB'
	}
}

root(Level.INFO, ["CONSOLE", "FILE"])
