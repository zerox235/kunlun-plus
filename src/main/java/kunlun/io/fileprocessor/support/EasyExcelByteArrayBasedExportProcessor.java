package kunlun.io.fileprocessor.support;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.constant.OrderConstant;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import kunlun.common.Page;
import kunlun.core.function.Consumer;
import kunlun.exception.ExceptionUtil;
import kunlun.io.fileprocessor.FileProcessor;
import kunlun.io.fileprocessor.ProcConfig;
import kunlun.io.fileprocessor.ProcResult;
import kunlun.io.fileprocessor.support.util.FileProcessUtils;
import kunlun.util.Assert;
import kunlun.util.CollUtil;
import kunlun.util.StrUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static kunlun.common.constant.Numbers.*;
import static kunlun.io.fileprocessor.ProcResult.Statistic;
import static kunlun.util.ObjUtil.cast;

public class EasyExcelByteArrayBasedExportProcessor implements FileProcessor {
    private static final Logger log = LoggerFactory.getLogger(EasyExcelByteArrayBasedExportProcessor.class);
    private static String asyncOutputMessage = "Please wait for a moment while data is being exported! ";
    private final List<List<String>> headers;
    private final Class<?> headerClass;
    private final String outputFilename;

    public EasyExcelByteArrayBasedExportProcessor(List<List<String>> headers,
                                                  String outputFilename) {
        this.outputFilename = Assert.notBlank(outputFilename);
        this.headers = Assert.notEmpty(headers);
        this.headerClass = null;
    }

    public EasyExcelByteArrayBasedExportProcessor(Class<?> headerClass,
                                                  String outputFilename) {
        this.outputFilename = Assert.notBlank(outputFilename);
        this.headerClass = Assert.notNull(headerClass);
        this.headers = null;
    }

    public static String getAsyncOutputMessage() {

        return asyncOutputMessage;
    }

    public static void setAsyncOutputMessage(String asyncOutputMessage) {

        EasyExcelByteArrayBasedExportProcessor.asyncOutputMessage = Assert.notBlank(asyncOutputMessage);
    }

    @Override
    public <P, R> ProcResult execute(ProcConfig<P, R> config) {
        // Build async supported context.
        ExcelExportContext<P, R> context = new ExcelExportContext<P, R>(config);
        context.setDataSupplier(Assert.notNull(config.getDataSupplier()));
        context.setOutputFilename(outputFilename);
        ProcResult result = context.getResult();
        // Get thread pool.
        FileProcessUtils.processThreadPool(context);
        //
        context.getContextConfigurator().accept(context);
        if (config.getPreprocessor() == null) {
            context.setPreprocessor(new Consumer<ProcContext<P, R>>(){
                @Override
                public void accept(ProcContext<P, R> context) {
                    try {
                        ExcelExportContext<P, R> nowContext = cast(context);
                        nowContext.setOutputStream(new ByteArrayOutputStream());
                        // Create excel writer builder.
                        ExcelWriterBuilder builder = EasyExcel.write(nowContext.getOutputStream());
                        //
                        builder.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy());
                        builder.registerWriteHandler(new SheetWriteHandler() {
                            @Override
                            public int order() { return OrderConstant.DEFAULT_ORDER; }
                            @Override
                            public void afterSheetCreate(WriteWorkbookHolder wwh, WriteSheetHolder wsh) {
                                Sheet sheet = wsh.getSheet();
                                sheet.createFreezePane(ZERO, ONE);
                            }
                        });
                        // Set headers (not necessary).
                        if (headerClass != null) {
                            builder.head(headerClass);
                        }
                        else if (CollUtil.isNotEmpty(headers)) {
                            builder.head(headers);
                        }
                        // Set excelWriter and writeSheet.
                        nowContext.setExcelWriter(builder.build());
                        nowContext.setWriteSheet(EasyExcel.writerSheet().build());
                    } catch (Exception e) { throw ExceptionUtil.wrap(e); }
                }
            });
        }
        // Check of context.
//        Assert.notBlank(context.getModule(), "Parameter \"module\" must not blank. ");
        // Default value processing.
        if (StrUtil.isBlank(result.getOutputMessage()) && context.getAsync()) {
            result.setOutputMessage(getAsyncOutputMessage());
        }
        // Push task before asynchronous for task ID.
        // Export status: 0 unknown, 1 will export, 2 exporting, 3 processing, 4 timeout, 5 failure, 6 success
        if (result.getStatus() == null || result.getStatus() == ONE) {
            result.setStatus(TWO);
            context.getStatusListener().accept(context);
        }
        // Execution logic.
        return (ProcResult) context.execute(this);
    }

    @Override
    public ProcResult doAsync(AsyncContext context) {
        ExcelExportContext<Object, Object> nowContext = cast(context);
        ProcResult result = nowContext.getResult();
        Statistic  statistic = result.getStatistic();
        try {
            // Write excel headers.
            nowContext.getPreprocessor().accept(nowContext);
            // Query the data and write it to excel.
            long successCount = ZERO;
            for (int pageNum = ONE, pageCount = ONE; pageNum <= pageCount; pageNum++) {
                // Query the data.
                Page<Object> page = nowContext.getDataSupplier().apply(nowContext, pageNum);
                // EasyExcel has to write out the data even if it doesn't have it.
                // Opening this excel (generated without calling the "write" method) will result in an error.
                if (page == null) { page = Page.of(Collections.emptyList()); }
                // Handle page count (only on the first page).
                // If the result is not instance of "PageArrayList", the data query is not paged.
                if (pageNum == ONE && page.getPageCount() != null) {
                    pageCount = page.getPageCount();
                    statistic.setTotalCount(page.getTotal());
                }
                // Write data to excel.
                WriteSheet writeSheet = nowContext.getWriteSheet();
                nowContext.getExcelWriter().write(page.getData(), writeSheet);
                //
                successCount += page.getData().size();
            }
            nowContext.getExcelWriter().close();
            statistic.setSuccessCount(successCount);
            // Calculate the count.
            Long totalCount = statistic.getTotalCount();
            if (totalCount == null) {
                statistic.setTotalCount(totalCount = successCount);
            }
            statistic.setFailureCount(totalCount - successCount);
            // Save result.
            nowContext.setOutputFileData(nowContext.getOutputStream().toByteArray());
            nowContext.getOutputSaver().accept(nowContext);
            // Push export task information.
            result.setEndTime(new Date());
            result.setStatus(SIX);
            nowContext.getStatusListener().accept(nowContext);
            // Get the result.
            nowContext.setFinish(true);
            return (ProcResult) nowContext.getResultExtractor().apply(nowContext);
        }
        catch (Exception e) {
            // Push export task information.
            result.setStatus(FIVE);
            nowContext.setError(e);
            nowContext.getStatusListener().accept(nowContext);
            throw ExceptionUtil.wrap(e);
        }
    }

    public static class ExcelExportContext<P, R> extends ProcContext<P, R> {
        private ByteArrayOutputStream outputStream;
        private ExcelWriter excelWriter;
        private WriteSheet writeSheet;

        // ====

        public ExcelExportContext(ProcConfig<P, R> config) {

            super(config);
        }

        public ExcelExportContext() {
        }

        public ByteArrayOutputStream getOutputStream() {

            return outputStream;
        }

        public void setOutputStream(ByteArrayOutputStream outputStream) {

            this.outputStream = outputStream;
        }

        public ExcelWriter getExcelWriter() {

            return excelWriter;
        }

        public void setExcelWriter(ExcelWriter excelWriter) {

            this.excelWriter = excelWriter;
        }

        public WriteSheet getWriteSheet() {

            return writeSheet;
        }

        public void setWriteSheet(WriteSheet writeSheet) {

            this.writeSheet = writeSheet;
        }
    }

}
