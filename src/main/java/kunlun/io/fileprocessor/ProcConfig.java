package kunlun.io.fileprocessor;

import kunlun.common.Page;
import kunlun.core.function.BiConsumer;
import kunlun.core.function.BiFunction;
import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.util.Assert;

import java.io.Serializable;

import static kunlun.core.Asynchronous.AsyncContext;
import static kunlun.io.fileprocessor.FileProcessor.ProcContext;

/**
 * ProcConfig.
 * @author Kahle
 */
public class ProcConfig<P, R> implements Serializable {

    private Consumer<ProcContext<P, R>> contextConfigurator;
    private Consumer<ProcContext<P, R>> statusListener;
    private Consumer<ProcContext<P, R>> preprocessor;
    private Consumer<ProcContext<P, R>> outputSaver;
    private Function<AsyncContext, Object> resultExtractor;

    // ====

    private BiFunction<ProcContext<P, R>, Object, Page<R>> dataSupplier;
    private BiConsumer<ProcContext<P, R>, Page<R>> dataConsumer;
    private FileProcessor fileProcessor;
    private P param;

    // ====

    public Consumer<ProcContext<P, R>> getContextConfigurator() {

        return contextConfigurator;
    }

    public ProcConfig<P, R> setContextConfigurator(Consumer<ProcContext<P, R>> contextConfigurator) {
        this.contextConfigurator = Assert.notNull(contextConfigurator);
        return this;
    }

    public Consumer<ProcContext<P, R>> getStatusListener() {

        return statusListener;
    }

    public ProcConfig<P, R> setStatusListener(Consumer<ProcContext<P, R>> statusListener) {
        this.statusListener = Assert.notNull(statusListener);
        return this;
    }

    public Consumer<ProcContext<P, R>> getPreprocessor() {

        return preprocessor;
    }

    public ProcConfig<P, R> setPreprocessor(Consumer<ProcContext<P, R>> preprocessor) {
        this.preprocessor = Assert.notNull(preprocessor);
        return this;
    }

    public Consumer<ProcContext<P, R>> getOutputSaver() {

        return outputSaver;
    }

    public ProcConfig<P, R> setOutputSaver(Consumer<ProcContext<P, R>> outputSaver) {
        this.outputSaver = Assert.notNull(outputSaver);
        return this;
    }

    public Function<AsyncContext, Object> getResultExtractor() {

        return resultExtractor;
    }

    public ProcConfig<P, R> setResultExtractor(Function<AsyncContext, Object> resultExtractor) {
        this.resultExtractor = Assert.notNull(resultExtractor);
        return this;
    }

    public BiFunction<ProcContext<P, R>, Object, Page<R>> getDataSupplier() {

        return dataSupplier;
    }

    public ProcConfig<P, R> setDataSupplier(BiFunction<ProcContext<P, R>, Object, Page<R>> dataSupplier) {
        this.dataSupplier = Assert.notNull(dataSupplier);
        return this;
    }

    public BiConsumer<ProcContext<P, R>, Page<R>> getDataConsumer() {

        return dataConsumer;
    }

    public ProcConfig<P, R> setDataConsumer(BiConsumer<ProcContext<P, R>, Page<R>> dataConsumer) {
        this.dataConsumer = Assert.notNull(dataConsumer);
        return this;
    }

    public FileProcessor getFileProcessor() {

        return fileProcessor;
    }

    public ProcConfig<P, R> setFileProcessor(FileProcessor fileProcessor) {
        this.fileProcessor = Assert.notNull(fileProcessor);
        return this;
    }

    public P getParam() {

        return param;
    }

    public ProcConfig<P, R> setParam(P param) {
        this.param = Assert.notNull(param);
        return this;
    }

    public ProcResult execute() {

        return Assert.notNull(getFileProcessor()).execute(this);
    }
}