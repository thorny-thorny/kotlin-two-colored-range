import kotlin.ranges.IntRange;
import me.thorny.twoColoredRange.RedGreenColor;
import me.thorny.twoColoredRange.RedGreenIntArrayRange;
import me.thorny.twoColoredRange.math.IntBoundMath;
import org.junit.Assert;
import org.junit.Test;

public class JavaTests {
    @Test
    public void testJavaInterop() {
        // Simple test to check if Kotlin classes can be used in Java code at all
        RedGreenIntArrayRange range = new RedGreenIntArrayRange(new IntRange(1, 2));
        Assert.assertEquals(new IntRange(1, 2), range.getRange());
        Assert.assertEquals(1, range.getStep().intValue());
        Assert.assertEquals(IntBoundMath.INSTANCE, range.getMath());
        Assert.assertEquals(RedGreenColor.Red, range.getDefaultColor());
        Assert.assertEquals(RedGreenColor.Green, range.getOtherColor());
        Assert.assertEquals(2, range.getLength().intValue());
    }
}
