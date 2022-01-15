package me.thorny.twoColoredRange

interface RangeFactory<BoundType: Comparable<BoundType>> {
  fun getRange(start: BoundType, endInclusive: BoundType): ClosedRange<BoundType>
}

open class ClosedRangeFactory<BoundType: Comparable<BoundType>>: RangeFactory<BoundType> {
  override fun getRange(start: BoundType, endInclusive: BoundType) = start..endInclusive
}

object IntRangeFactory: RangeFactory<Int> {
  override fun getRange(start: Int, endInclusive: Int) = IntRange(start, endInclusive)
}

object LongRangeFactory: RangeFactory<Long> {
  override fun getRange(start: Long, endInclusive: Long) = LongRange(start, endInclusive)
}
