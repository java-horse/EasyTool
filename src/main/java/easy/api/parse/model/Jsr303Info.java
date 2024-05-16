package easy.api.parse.model;

import java.math.BigDecimal;

/**
 * JSR303注解信息
 */
public class Jsr303Info {

    /**
     * 最小值
     */
    private BigDecimal minimum = null;

    /**
     * 最大值
     */
    private BigDecimal maximum = null;

    /**
     * 最小元素个数
     */
    private Integer minLength;

    /**
     * 最大元素个数
     */
    private Integer maxLength;

    public BigDecimal getMinimum() {
        return minimum;
    }

    public void setMinimum(BigDecimal minimum) {
        this.minimum = minimum;
    }

    public BigDecimal getMaximum() {
        return maximum;
    }

    public void setMaximum(BigDecimal maximum) {
        this.maximum = maximum;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}
