package me.thorny.twoColoredRange.rangeUtils

import me.thorny.twoColoredRange.math.BoundMath

/**
 * Checks if [other] range is entirely inside [this].
 */
fun <BoundType: Comparable<BoundType>> ClosedRange<BoundType>.containsRange(
  other: ClosedRange<BoundType>,
): Boolean {
  return this.start <= other.start && this.endInclusive >= other.endInclusive
}

/**
 * Checks if [other] range intersects [this].
 */
fun <BoundType: Comparable<BoundType>> ClosedRange<BoundType>.intersectsRange(
  other: ClosedRange<BoundType>,
): Boolean {
  return this.start <= other.endInclusive && this.endInclusive >= other.start
}

/**
 * Returns a new range by joining [other] range and [this].
 *
 * @param other the other range.
 * @param rangeFactory the range factory.
 */
fun <BoundType: Comparable<BoundType>> ClosedRange<BoundType>.joinedByRange(
  other: ClosedRange<BoundType>,
  rangeFactory: RangeFactory<BoundType>,
): ClosedRange<BoundType> {
  return rangeFactory.makeRange(minOf(this.start, other.start), maxOf(this.endInclusive, other.endInclusive))
}

/**
 * Returns a new range by trimming [this] by [other]'s bounds.
 *
 * @param other the other range.
 * @param rangeFactory the range factory.
 */
fun <BoundType: Comparable<BoundType>> ClosedRange<BoundType>.trimmedByRange(
  other: ClosedRange<BoundType>,
  rangeFactory: RangeFactory<BoundType>,
): ClosedRange<BoundType> {
  return rangeFactory.makeRange(maxOf(this.start, other.start), minOf(this.endInclusive, other.endInclusive))
}

/**
 * Checks if [other] range is located on a distance of [step] away from [this].
 *
 * @param other the other range.
 * @param step the distance.
 * @param math the math.
 */
fun <BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> ClosedRange<BoundType>.touchesRange(
  other: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
): Boolean {
  return math.add(this.endInclusive, step) == other.start || math.add(other.endInclusive, step) == this.start
}

/**
 * Cuts [other] range from [this] and returns a list of non-empty pieces that are left.
 *
 * @param other the other range.
 * @param step minimal non-zero range length.
 * @param math the math.
 */
fun <BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> ClosedRange<BoundType>.splitByRange(
  other: ClosedRange<BoundType>,
  step: LengthType,
  math: BoundMath<BoundType, LengthType>,
  rangeFactory: RangeFactory<BoundType>,
): List<ClosedRange<BoundType>> {
  return listOf(
    rangeFactory.makeRange(this.start, math.subtract(other.start, step)),
    rangeFactory.makeRange(math.add(other.endInclusive, step), this.endInclusive),
  ).filterNot(ClosedRange<BoundType>::isEmpty)
}
