package me.thorny.twoColoredRange

fun <T: Comparable<T>> ClosedRange<T>.containsRange(other: ClosedRange<T>): Boolean {
  return this.start <= other.start && this.endInclusive >= other.endInclusive
}

fun <T: Comparable<T>> ClosedRange<T>.intersectsRange(other: ClosedRange<T>): Boolean {
  return this.start <= other.endInclusive && this.endInclusive >= other.start
}

fun <T: Comparable<T>> ClosedRange<T>.makeTypedRange(start: T, endInclusive: T): ClosedRange<T> {
  @Suppress("UNCHECKED_CAST")
  return when (this) {
    is IntRange -> IntRange(start as Int, endInclusive as Int) as ClosedRange<T>
    is LongRange -> LongRange(start as Long, endInclusive as Long) as ClosedRange<T>
    else -> start..endInclusive
  }
}

fun <T: Comparable<T>> ClosedRange<T>.joinRange(other: ClosedRange<T>): ClosedRange<T> {
  return makeTypedRange(minOf(this.start, other.start), maxOf(this.endInclusive, other.endInclusive))
}

fun <BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> ClosedRange<BoundType>.touchesRange(
  other: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
): Boolean {
  return math.add(this.endInclusive, step) == other.start || math.add(other.endInclusive, step) == this.start
}

fun <BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> ClosedRange<BoundType>.splitByRange(
  other: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
): List<ClosedRange<BoundType>> {
  return listOf(
    makeTypedRange(this.start, math.subtract(other.start, step)),
    makeTypedRange(math.add(other.endInclusive, step), this.endInclusive),
  ).filter { it.start <= it.endInclusive }
}
