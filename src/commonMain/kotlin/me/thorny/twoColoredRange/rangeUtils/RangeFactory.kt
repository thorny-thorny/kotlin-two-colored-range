package me.thorny.twoColoredRange.rangeUtils

/**
 * Factory for ranges.
 */
interface RangeFactory<BoundType: Comparable<BoundType>> {
  /**
   * Creates a range from [start] to [endInclusive].
   */
  fun getRange(start: BoundType, endInclusive: BoundType): ClosedRange<BoundType>
}

/**
 * Default range factory, it should work for any [BoundType].
 */
open class ClosedRangeFactory<BoundType: Comparable<BoundType>>: RangeFactory<BoundType> {
  override fun getRange(start: BoundType, endInclusive: BoundType) = start..endInclusive
}

/**
 * Range factory for [IntRange].
 */
object IntRangeFactory: RangeFactory<Int> {
  override fun getRange(start: Int, endInclusive: Int) = IntRange(start, endInclusive)
}

/**
 * Range factory for [LongRange].
 */
object LongRangeFactory: RangeFactory<Long> {
  override fun getRange(start: Long, endInclusive: Long) = LongRange(start, endInclusive)
}
