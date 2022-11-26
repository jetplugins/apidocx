package io.apidocx.parse.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * JSR303注解信息
 */
@Data
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

}
