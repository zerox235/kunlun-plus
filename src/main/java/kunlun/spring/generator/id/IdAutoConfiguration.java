package kunlun.spring.generator.id;

import kunlun.generator.id.IdUtil;
import kunlun.generator.id.support.AbstractIncrementalIdGenerator;
import kunlun.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Map;

@Configuration
public class IdAutoConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(IdAutoConfiguration.class);

    @Resource
    private ApplicationContext appContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, AbstractIncrementalIdGenerator> generatorMap =
                appContext.getBeansOfType(AbstractIncrementalIdGenerator.class);
        for (AbstractIncrementalIdGenerator generator : generatorMap.values()) {
            if (generator == null) { continue; }
            String genName = generator.getConfig() != null ? generator.getConfig().getName() : null;
            if (StrUtil.isBlank(genName)) {
                log.warn("The id generator \"{}\"'s name is blank, it will be ignored. ", generator.getClass());
                continue;
            }
            IdUtil.registerGenerator(genName, generator);
        }
    }
}
