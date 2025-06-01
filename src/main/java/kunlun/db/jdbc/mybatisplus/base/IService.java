package kunlun.db.jdbc.mybatisplus.base;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.base.MPJBaseService;

import java.util.Collection;
import java.util.List;

/**
 * IService
 * @author Kahle
 */
@SuppressWarnings({"unused"})
public interface IService<T> extends MPJBaseService<T> {

    // region ======== exist related methods ========
    /**
     * Determine whether a record exists based on the input field and value.
     * @param field The input field
     * @param value The input value
     * @return Is existed
     */
    boolean existBy(SFunction<T, ?> field, Object value);

    /**
     * Determine whether a record exists based on the input field and value (no equal by another field).
     * @param field The input field
     * @param value The input value
     * @param neField The no equal field
     * @param neValue The no equal value
     * @return Is existed
     */
    boolean existBy(SFunction<T, ?> field, Object value, SFunction<T, ?> neField, Object neValue);
    // endregion ======== exist related methods ========


    // region ======== update related methods ========
    /**
     * Update the data based on the input field and value.
     * @param update That will update entity
     * @param field The input field
     * @param value The input value
     * @return Is update
     */
    boolean updateBy(T update, SFunction<T, ?> field, Object value);

    /**
     * Update the data based on the input field and values.
     * @param update That will update entity
     * @param field The input field
     * @param values The input values
     * @return Is update
     */
    boolean updateBy(T update, SFunction<T, ?> field, Collection<?> values);
    // endregion ======== update related methods ========


    // region ======== delete related methods ========
    /**
     * Delete data based on the input field and value.
     * @param field The input field
     * @param value The input value
     * @return Is delete
     */
    boolean deleteBy(SFunction<T, ?> field, Object value);

    /**
     * Delete data based on the input field and values.
     * @param field The input field
     * @param values The input values
     * @return Is delete
     */
    boolean deleteBy(SFunction<T, ?> field, Collection<?> values);
    // endregion ======== delete related methods ========


    // region ======== query related methods ========
    /**
     * Get the single data based on the input field and value.
     * @param field The input field
     * @param value The input value
     * @return The data or null
     */
    T getBy(SFunction<T, ?> field, Object value);

    /**
     * Find the data based on the input field and values
     * @param field The input field
     * @param values The input values
     * @return The list or null
     */
    List<T> findBy(SFunction<T, ?> field, Collection<?> values);
    // endregion ======== query related methods ========

}
