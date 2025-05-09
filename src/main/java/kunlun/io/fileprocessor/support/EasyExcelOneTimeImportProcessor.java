package kunlun.io.fileprocessor.support;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.constant.OrderConstant;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import kunlun.common.Page;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static kunlun.common.constant.Numbers.*;
import static kunlun.io.fileprocessor.ProcResult.Statistic;
import static kunlun.util.ObjUtil.cast;

public class EasyExcelOneTimeImportProcessor implements FileProcessor {
    private static final Logger log = LoggerFactory.getLogger(EasyExcelOneTimeImportProcessor.class);
    private static String asyncOutputMessage = "Please wait for a moment while the data is being imported! ";
    private final List<List<String>> headers;
    private final Class<?> headerClass;
    private final String outputFilename;
    private Integer headRowNumber;

//    public EasyExcelOneTimeImportProcessor(List<List<String>> headers,
//                                           String outputFilename) {
//        this.outputFilename = Assert.notBlank(outputFilename);
//        this.headers = Assert.notEmpty(headers);
//        this.headerClass = null;
//    }

    public EasyExcelOneTimeImportProcessor(Class<?> headerClass,
                                           String outputFilename) {
        this.outputFilename = Assert.notBlank(outputFilename);
        this.headerClass = Assert.notNull(headerClass);
        this.headers = null;
    }

    public Integer getHeadRowNumber() {
        return headRowNumber;
    }

    public EasyExcelOneTimeImportProcessor setHeadRowNumber(Integer headRowNumber) {
        this.headRowNumber = headRowNumber;
        return this;
    }

    public static String getAsyncOutputMessage() {

        return asyncOutputMessage;
    }

    public static void setAsyncOutputMessage(String asyncOutputMessage) {

        EasyExcelOneTimeImportProcessor.asyncOutputMessage = Assert.notBlank(asyncOutputMessage);
    }

    protected <P, R> InputStream parseFile(ExcelImportContext<P, R> context) {
        Object fileData = Assert.notNull(context.getOriginalFileData());
        try {
            if (fileData instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) fileData;
                if (StrUtil.isBlank(context.getOriginalFilename())) {
                    context.setOriginalFilename(file.getOriginalFilename());
                }
                context.getRuntimeData().put("fileContentType", file.getContentType());
                context.getRuntimeData().put("fileSize", file.getSize());
                return file.getInputStream();
            } else if (fileData instanceof File) {
                File file = (File) fileData;
                if (StrUtil.isBlank(context.getOriginalFilename())) {
                    context.setOriginalFilename(file.getName());
                }
                return new FileInputStream(file);
            } else if (fileData instanceof byte[]) {
                byte[] bytes = (byte[]) fileData;
                return new ByteArrayInputStream(bytes);
            } else {
                throw new IllegalArgumentException(
                        "The type of parameter \"fileData\" is not supported. "
                );
            }
        } catch (Exception e) { throw ExceptionUtil.wrap(e); }
    }

    @Override
    public <P, R> ProcResult execute(ProcConfig<P, R> config) {
        // Build async supported context.
        ExcelImportContext<P, R> context = new ExcelImportContext<P, R>(config);
        context.setDataConsumer(Assert.notNull(config.getDataConsumer()));
        context.setOutputFilename(outputFilename);
        ProcResult result = context.getResult();
        //
        P param = context.getConfig().getParam();
        if (param instanceof MultipartFile
                || param instanceof File
                || param instanceof byte[]) {
            context.setOriginalFileData(param);
        }
        // Get thread pool.
        FileProcessUtils.processThreadPool(context);
        //
        context.getContextConfigurator().accept(context);
        // Check of context.
        // Default value processing.
        if (StrUtil.isBlank(result.getOutputMessage()) && context.getAsync()) {
            result.setOutputMessage(getAsyncOutputMessage());
        }
        if (context.getReadSheets() == null) { context.setReadSheets(new ArrayList<ReadSheet>()); }
        if (context.getParsedData() == null) { context.setParsedData(new ArrayList<R>()); }
        // Parse file data.
        Object fileData = context.getOriginalFileData();
        Assert.notNull(fileData, "Parameter \"fileData\" must not null. ");
        InputStream inputStream = parseFile(context);
        ExcelReader excelReader = EasyExcel.read(inputStream).build();
        context.setExcelReader(excelReader);
        //
        if (CollUtil.isEmpty(context.getReadSheets())) {
            ExcelReaderSheetBuilder sheetBuilder = EasyExcel.readSheet()
                    .head(headerClass)
                    .registerReadListener(new SimpleDataReadListener<R>(context.getParsedData()));
            if (getHeadRowNumber() != null) {
                sheetBuilder.headRowNumber(getHeadRowNumber());
            }
            context.getReadSheets().add(sheetBuilder.build());
        }
        //
        excelReader.read(context.getReadSheets());
        // Push task before asynchronous for task ID.
        // Import status: 0 unknown, 1 will import, 2 importing, 3 processing, 4 timeout, 5 failure, 6 success
        if (result.getStatus() == null || result.getStatus() == ONE) {
            result.setStatus(TWO);
            context.getStatusListener().accept(context);
        }
        // Execution logic.
        return (ProcResult) context.execute(this);
    }

    @Override
    public ProcResult doAsync(AsyncContext context) {
        ExcelImportContext<Object, Object> nowContext = cast(context);
        ProcResult result = nowContext.getResult();
        Statistic  statistic = result.getStatistic();
        try {
            // Why not use something like paging?
            // Because "ExcelReader" already loads the entire file into memory.
            // So the current class only supports small amount of data in excel files.
            // So the data read logic is directly read all at once.
            if (nowContext.getPreprocessor() != null) {
                nowContext.getPreprocessor().accept(nowContext);
            }
            //
            List<Object> parsedData = nowContext.getParsedData();
            if (statistic.getTotalCount() == null) {
                statistic.setTotalCount(0L);
                if (CollUtil.isNotEmpty(parsedData)) {
                    statistic.setTotalCount(((Integer) parsedData.size()).longValue());
                }
            }
            // Perform data processing, such as storing to a database.
            nowContext.getDataConsumer().accept(nowContext, Page.of(parsedData));
            // Save the result of the import.
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            EasyExcel.write(output, headerClass)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerWriteHandler(new SheetWriteHandler() {
                        @Override
                        public int order() { return OrderConstant.DEFAULT_ORDER; }
                        @Override
                        public void afterSheetCreate(WriteWorkbookHolder wwh, WriteSheetHolder wsh) {
                            Sheet sheet = wsh.getSheet();
                            sheet.createFreezePane(ZERO, ONE);
                        }
                    })
                    .sheet().doWrite(parsedData);
            nowContext.setOutputFileData(output.toByteArray());
            nowContext.getOutputSaver().accept(nowContext);
            // Push import task information.
            // Task status is assigned by specific processing logic.
            if (result.getEndTime() == null) {
                result.setEndTime(new Date());
            }
            result.setStatus(SIX);
            nowContext.getStatusListener().accept(nowContext);
            // Get the result.
            nowContext.setFinish(true);
            return (ProcResult) nowContext.getResultExtractor().apply(nowContext);
        }
        catch (Exception e) {
            // Push import task information.
            result.setStatus(FIVE);
            nowContext.setError(e);
            nowContext.getStatusListener().accept(nowContext);
            throw ExceptionUtil.wrap(e);
        }
    }

    public static class ExcelImportContext<P, R> extends ProcContext<P, R> {
        private List<ReadSheet> readSheets;
        private ExcelReader excelReader;
        private List<R> parsedData;

        // ====

        public ExcelImportContext(ProcConfig<P, R> config) {

            super(config);
        }

        public ExcelImportContext() {
        }

        public List<ReadSheet> getReadSheets() {

            return readSheets;
        }

        public void setReadSheets(List<ReadSheet> readSheets) {

            this.readSheets = readSheets;
        }

        public ExcelReader getExcelReader() {

            return excelReader;
        }

        public void setExcelReader(ExcelReader excelReader) {

            this.excelReader = excelReader;
        }

        public List<R> getParsedData() {

            return parsedData;
        }

        public void setParsedData(List<R> parsedData) {

            this.parsedData = parsedData;
        }
    }

    protected static class SimpleDataReadListener<R> implements ReadListener<R> {
        private final List<R> parsedData;
        public SimpleDataReadListener(List<R> parsedData) {

            this.parsedData = Assert.notNull(parsedData);
        }
        @Override
        public void invoke(R data, AnalysisContext context) { this.parsedData.add(data); }
        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {}
    }

}
