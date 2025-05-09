package kunlun.io.fileprocessor;

import kunlun.common.Page;
import kunlun.core.Asynchronous;
import kunlun.core.function.BiConsumer;
import kunlun.core.function.BiFunction;
import kunlun.core.function.Consumer;
import kunlun.core.function.Function;
import kunlun.io.FileBase;
import kunlun.io.FileObject;
import kunlun.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static kunlun.common.constant.Numbers.ONE;

public interface FileProcessor extends Asynchronous {

    <P, R> ProcResult execute(ProcConfig<P, R> config);

    class ProcContext<P, R> extends AsyncContext {

        // region ======== runtime data, config, result and error ========
        /**
         * runtimeData
         */
        private final Map<String, Object> runtimeData = new ConcurrentHashMap<String, Object>();
        /**
         * The import or export configuration.
         */
        private ProcConfig<P, R> config;
        /**
         * result
         */
        private ProcResult result;
        /**
         * An error occurred while importing or exporting.
         */
        private Exception error;
        // endregion


        // region ======== consumer ========
        /**
         * context Processor | buildContext
         */
        private Consumer<ProcContext<P, R>> contextConfigurator;
        /**
         * pushTask
         */
        private Consumer<ProcContext<P, R>> statusListener;
        /**
         * preHandle
         */
        private Consumer<ProcContext<P, R>> preprocessor;
        /**
         * Save the data (biased to IO write).
         */
        private Consumer<ProcContext<P, R>> outputSaver;
        // endregion


        // region ======== original files and output files ========
        /**
         * originalFiles
         */
        private List<FileObject> originalFiles;
        /**
         * outputFiles
         */
        private List<FileObject> outputFiles;


        /**
         * originalFilename
         */
        @Deprecated
        private String originalFilename;
        /**
         * originalFileData
         */
        @Deprecated
        private Object originalFileData;
        /**
         * outputFilename
         */
        @Deprecated
        private String outputFilename;
        /**
         * outputFileData
         */
        @Deprecated
        private Object outputFileData;
        // endregion


        // region ======== data supplier / consumer ========
        /**
         * dataSupplier
         */
        private BiFunction<ProcContext<P, R>, Object, Page<R>> dataSupplier;
        /**
         * dataConsumer
         */
        private BiConsumer<ProcContext<P, R>, Page<R>> dataConsumer;
        // endregion


        // region ======== constructor ========

        public ProcContext(ProcConfig<P, R> config) {
            this();
            this.setConfig(Assert.notNull(config));
            if (config.getContextConfigurator() != null) {
                this.setContextConfigurator(config.getContextConfigurator());
            }
            if (config.getStatusListener() != null) {
                this.setStatusListener(config.getStatusListener());
            }
            if (config.getPreprocessor() != null) {
                this.setPreprocessor(config.getPreprocessor());
            }
            if (config.getOutputSaver() != null) {
                this.setOutputSaver(config.getOutputSaver());
            }
            if (config.getResultExtractor() != null) {
                this.setResultExtractor(config.getResultExtractor());
            }
            if (config.getDataSupplier() != null) {
                this.setDataSupplier(config.getDataSupplier());
            }
            if (config.getDataConsumer() != null) {
                this.setDataConsumer(config.getDataConsumer());
            }
        }

        public ProcContext() {
            Consumer<ProcContext<P, R>> emptyConsumer = new Consumer<ProcContext<P, R>>() {
                @Override
                public void accept(ProcContext<P, R> context) {

                }
            };
            this.setResult(new ProcResult());
            this.setContextConfigurator(emptyConsumer);
            this.setStatusListener(emptyConsumer);
            this.setPreprocessor(emptyConsumer);
            this.setOutputSaver(emptyConsumer);
            this.setOriginalFiles(new ArrayList<FileObject>());
            this.setOutputFiles(new ArrayList<FileObject>());
            this.setResultExtractor(new Function<AsyncContext, Object>() {
                @Override
                public Object apply(AsyncContext context) {
                    //noinspection unchecked
                    return ((ProcContext<P, R>) context).getResult();
                }
            });
            getResult().setOutputFiles(new ArrayList<FileBase>());
            getResult().setStatistic(new ProcResult.Statistic());
            getResult().setBeginTime(new Date());
            getResult().setStatus(ONE);
        }
        // endregion


        // region ======== getter and setter ========

        public Map<String, Object> getRuntimeData() {

            return runtimeData;
        }

        public ProcConfig<P, R> getConfig() {

            return config;
        }

        public void setConfig(ProcConfig<P, R> config) {

            this.config = config;
        }

        public ProcResult getResult() {

            return result;
        }

        public void setResult(ProcResult result) {

            this.result = result;
        }

        public Exception getError() {

            return error;
        }

        public void setError(Exception error) {

            this.error = error;
        }

        public Consumer<ProcContext<P, R>> getContextConfigurator() {

            return contextConfigurator;
        }

        public void setContextConfigurator(Consumer<ProcContext<P, R>> contextConfigurator) {

            this.contextConfigurator = contextConfigurator;
        }

        public Consumer<ProcContext<P, R>> getStatusListener() {

            return statusListener;
        }

        public void setStatusListener(Consumer<ProcContext<P, R>> statusListener) {

            this.statusListener = statusListener;
        }

        public Consumer<ProcContext<P, R>> getPreprocessor() {

            return preprocessor;
        }

        public void setPreprocessor(Consumer<ProcContext<P, R>> preprocessor) {

            this.preprocessor = preprocessor;
        }

        public Consumer<ProcContext<P, R>> getOutputSaver() {

            return outputSaver;
        }

        public void setOutputSaver(Consumer<ProcContext<P, R>> outputSaver) {

            this.outputSaver = outputSaver;
        }

        public List<FileObject> getOriginalFiles() {

            return originalFiles;
        }

        public void setOriginalFiles(List<FileObject> originalFiles) {

            this.originalFiles = originalFiles;
        }

        public List<FileObject> getOutputFiles() {

            return outputFiles;
        }

        public void setOutputFiles(List<FileObject> outputFiles) {

            this.outputFiles = outputFiles;
        }

        @Deprecated
        public String getOriginalFilename() {

            return originalFilename;
        }

        @Deprecated
        public void setOriginalFilename(String originalFilename) {

            this.originalFilename = originalFilename;
        }

        @Deprecated
        public Object getOriginalFileData() {

            return originalFileData;
        }

        @Deprecated
        public void setOriginalFileData(Object originalFileData) {

            this.originalFileData = originalFileData;
        }

        @Deprecated
        public String getOutputFilename() {

            return outputFilename;
        }

        @Deprecated
        public void setOutputFilename(String outputFilename) {

            this.outputFilename = outputFilename;
        }

        @Deprecated
        public Object getOutputFileData() {

            return outputFileData;
        }

        @Deprecated
        public void setOutputFileData(Object outputFileData) {

            this.outputFileData = outputFileData;
        }

        public BiFunction<ProcContext<P, R>, Object, Page<R>> getDataSupplier() {

            return dataSupplier;
        }

        public void setDataSupplier(BiFunction<ProcContext<P, R>, Object, Page<R>> dataSupplier) {

            this.dataSupplier = dataSupplier;
        }

        public BiConsumer<ProcContext<P, R>, Page<R>> getDataConsumer() {

            return dataConsumer;
        }

        public void setDataConsumer(BiConsumer<ProcContext<P, R>, Page<R>> dataConsumer) {

            this.dataConsumer = dataConsumer;
        }
        // endregion
    }

}
