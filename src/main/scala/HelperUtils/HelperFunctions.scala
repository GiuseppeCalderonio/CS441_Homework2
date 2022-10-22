package HelperUtils

import com.mifmif.common.regex.Generex
import dk.brics.automaton.{RegExp, State, Transition}

import java.io.File
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.annotation.tailrec
import scala.beans.BeanProperty
import scala.collection.mutable
import scala.util.Random
import scala.util.matching.Regex

/**
 * This object represents a set of functions, mainly used to generate random strings matching a regexp or
 * belonging to a time interval
 */
object HelperFunctions {

        private val timeRegexp = new Regex(Parameters.timeRegexp)
        private val messageTypes = new Regex(Parameters.messageTypes)




        /**
         * Generates a random String based on the given regular expression
         *
         * @param regex the regexp that the output string will match
         * @return a random string matching the regexp pattern
         */
        def generate(regex: String): String = {
                new Generex (regex).random ()
        }


}