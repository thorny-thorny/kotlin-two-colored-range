package me.thorny.twoColoredRange

fun <T: Comparable<T>> ClosedRange<T>.containsRange(other: ClosedRange<T>): Boolean {
  return this.start <= other.start && this.endInclusive >= other.endInclusive
}

fun <T: Comparable<T>> ClosedRange<T>.intersectsRange(other: ClosedRange<T>): Boolean {
  return this.start <= other.endInclusive && this.endInclusive >= other.start
}

fun <T: Comparable<T>> ClosedRange<T>.joinRange(other: ClosedRange<T>): ClosedRange<T> {
  val start = minOf(this.start, other.start)
  val endInclusive = maxOf(this.endInclusive, other.endInclusive)
  @Suppress("UNCHECKED_CAST")
  return when (this) {
    is IntRange -> IntRange(start as Int, endInclusive as Int) as ClosedRange<T>
    else -> start..endInclusive
  }
}
