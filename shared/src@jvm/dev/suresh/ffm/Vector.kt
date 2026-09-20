@file:Suppress("UNCHECKED_CAST")

package dev.suresh.ffm

import jdk.incubator.vector.*

// Generic Vector-to-Vector Operations
operator fun <E, V : Vector<E>> V.plus(other: V): V = add(other) as V

operator fun <E, V : Vector<E>> V.minus(other: V): V = sub(other) as V

operator fun <E, V : Vector<E>> V.times(other: V): V = mul(other) as V

operator fun <E, V : Vector<E>> V.div(other: V): V = div(other) as V

operator fun <E, V : Vector<E>> V.unaryMinus(): V = neg() as V

operator fun <E, V : Vector<E>> V.unaryPlus(): V = this

// Index Access
operator fun FloatVector.get(index: Int): Float = lane(index)

operator fun IntVector.get(index: Int): Int = lane(index)

operator fun LongVector.get(index: Int): Long = lane(index)

operator fun DoubleVector.get(index: Int): Double = lane(index)

operator fun ByteVector.get(index: Int): Byte = lane(index)

operator fun ShortVector.get(index: Int): Short = lane(index)
