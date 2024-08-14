package me.thorny.twoColoredRange.math

/**
 * Math used by colored ranges. See [TwoColoredRange][me.thorny.twoColoredRange.TwoColoredRange] for details.
 */
interface BoundMath<BoundType: Comparable<BoundType>, LengthType: Comparable<LengthType>> {
  /**
   * Returns [bound] + [length].
   */
  fun add(bound: BoundType, length: LengthType): BoundType

  /**
   * Returns [bound] - [length].
   */
  fun subtract(bound: BoundType, length: LengthType): BoundType

  /**
   * Returns [endExclusive] - [start].
   */
  fun getLength(start: BoundType, endExclusive: BoundType): LengthType
}

/**
 * Math for colored [Int] ranges.
 */
object IntBoundMath: BoundMath<Int, Int> {
  override fun add(bound: Int, length: Int) = bound + length
  override fun subtract(bound: Int, length: Int) = bound - length
  override fun getLength(start: Int, endExclusive: Int) = endExclusive - start
}

/**
 * Math for colored [Long] ranges.
 */
object LongBoundMath: BoundMath<Long, Long> {
  override fun add(bound: Long, length: Long) = bound + length
  override fun subtract(bound: Long, length: Long) = bound - length
  override fun getLength(start: Long, endExclusive: Long) = endExclusive - start
}
