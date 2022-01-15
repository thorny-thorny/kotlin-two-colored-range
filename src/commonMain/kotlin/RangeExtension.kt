package me.thorny.twoColoredRange

fun <BoundType: Comparable<BoundType>> ClosedRange<BoundType>.containsRange(
  other: ClosedRange<BoundType>,
): Boolean {
  return this.start <= other.start && this.endInclusive >= other.endInclusive
}

fun <BoundType: Comparable<BoundType>> ClosedRange<BoundType>.intersectsRange(
  other: ClosedRange<BoundType>,
): Boolean {
  return this.start <= other.endInclusive && this.endInclusive >= other.start
}

fun <BoundType: Comparable<BoundType>> ClosedRange<BoundType>.joinRange(
  other: ClosedRange<BoundType>,
  rangeFactory: RangeFactory<BoundType>,
): ClosedRange<BoundType> {
  return rangeFactory.getRange(minOf(this.start, other.start), maxOf(this.endInclusive, other.endInclusive))
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
  rangeFactory: RangeFactory<BoundType>,
): List<ClosedRange<BoundType>> {
  return listOf(
    rangeFactory.getRange(this.start, math.subtract(other.start, step)),
    rangeFactory.getRange(math.add(other.endInclusive, step), this.endInclusive),
  ).filter { it.start <= it.endInclusive }
}
