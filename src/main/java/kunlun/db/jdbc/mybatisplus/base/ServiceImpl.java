package kunlun.db.jdbc.mybatisplus.base;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.base.MPJBaseServiceImpl;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singletonList;
import static kunlun.common.constant.Numbers.ZERO;
import static kunlun.util.Assert.notEmpty;
import static kunlun.util.Assert.notNull;

/**
 * ServiceImpl
 * @author Kahle
 */
@SuppressWarnings({"unused"})
public abstract class ServiceImpl<M extends MPJBaseMapper<T>, T> extends MPJBaseServiceImpl<M, T> implements IService<T> {

    // region ======== exist related methods ========

    @Override
    public boolean existBy(SFunction<T, ?> field, Object value) {
        return count(Wrappers.lambdaQuery(getEntityClass())
                .in(notNull(field), notNull(value))
        ) > ZERO;
    }

    @Override
    public boolean existBy(SFunction<T, ?> field, Object value, SFunction<T, ?> neField, Object neValue) {
        boolean neValNotEmp = ObjUtil.isNotEmpty(neValue);
        if (neValNotEmp) { notNull(neField); }
        return count(Wrappers.lambdaQuery(getEntityClass())
                .eq(notNull(field), notNull(value))
                .ne(neValNotEmp, neField, neValue)
        ) > ZERO;
    }
    // endregion ======== exist related methods ========


    // region ======== update related methods ========

    @Override
    public boolean updateBy(T update, SFunction<T, ?> field, Object value) {

        return updateBy(update, field, singletonList(notNull(value)));
    }

    @Override
    public boolean updateBy(T update, SFunction<T, ?> field, Collection<?> values) {
        return update(notNull(update), Wrappers.lambdaUpdate(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(values)))
        );
    }
    // endregion ======== update related methods ========


    // region ======== delete related methods ========

    @Override
    public boolean deleteBy(SFunction<T, ?> field, Object value) {

        return deleteBy(notNull(field), singletonList(notNull(value)));
    }

    @Override
    public boolean deleteBy(SFunction<T, ?> field, Collection<?> values) {
        return remove(Wrappers.lambdaQuery(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(values)))
        );
    }
    // endregion ======== delete related methods ========


    // region ======== query related methods ========

    @Override
    public T getBy(SFunction<T, ?> field, Object value) {

        return CollUtil.getFirst(findBy(notNull(field), singletonList(notNull(value))));
    }

    @Override
    public List<T> findBy(SFunction<T, ?> field, Collection<?> values) {
        return list(Wrappers.lambdaQuery(getEntityClass())
                .in(notNull(field), CollUtil.distinct(notEmpty(values)))
        );
    }
    // endregion ======== query related methods ========

}
