package kunlun.io.fileprocessor;

import kunlun.io.FileBase;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ProcResult.
 * @author Kahle
 */
public class ProcResult implements Serializable {
    /**
     * The identifier of the import or export task.
     */
    private String taskId;
    /**
     * The status: 0 unknown, 1 will process, 2 preprocess, 3 processing, 4 timeout(dead), 5 failure, 6 success
     */
    private Integer status;
    /**
     * The beginning time of the task.
     */
    private Date beginTime;
    /**
     * The end time of the task.
     */
    private Date endTime;
    /**
     * Import or export the result address
     */
    @Deprecated
    private Object outputData;
    /**
     * Import or export the result message
     */
    private String outputMessage;
    /**
     * Import or export the result address.
     */
    private List<FileBase> outputFiles;
    /**
     * statistic
     */
    private Statistic statistic;

    public String getTaskId() {

        return taskId;
    }

    public void setTaskId(String taskId) {

        this.taskId = taskId;
    }

    public Integer getStatus() {

        return status;
    }

    public void setStatus(Integer status) {

        this.status = status;
    }

    public Date getBeginTime() {

        return beginTime;
    }

    public void setBeginTime(Date beginTime) {

        this.beginTime = beginTime;
    }

    public Date getEndTime() {

        return endTime;
    }

    public void setEndTime(Date endTime) {

        this.endTime = endTime;
    }

    @Deprecated
    public Object getOutputData() {

        return outputData;
    }

    @Deprecated
    public void setOutputData(Object outputData) {

        this.outputData = outputData;
    }

    public List<FileBase> getOutputFiles() {

        return outputFiles;
    }

    public void setOutputFiles(List<FileBase> outputFiles) {

        this.outputFiles = outputFiles;
    }

    public String getOutputMessage() {

        return outputMessage;
    }

    public void setOutputMessage(String outputMessage) {

        this.outputMessage = outputMessage;
    }

    public Statistic getStatistic() {

        return statistic;
    }

    public void setStatistic(Statistic statistic) {

        this.statistic = statistic;
    }

    // ====

    /**
     * Statistic.
     * @author Kahle
     */
    public static class Statistic {
        /**
         * The total number of items to be imported or exported.
         */
        private Long totalCount;
        /**
         * The number of successful imports or exports.
         */
        private Long successCount;
        /**
         * The number of import or export failures.
         */
        private Long failureCount;

        public Long getTotalCount() {

            return totalCount;
        }

        public void setTotalCount(Long totalCount) {

            this.totalCount = totalCount;
        }

        public Long getSuccessCount() {

            return successCount;
        }

        public void setSuccessCount(Long successCount) {

            this.successCount = successCount;
        }

        public Long getFailureCount() {

            return failureCount;
        }

        public void setFailureCount(Long failureCount) {

            this.failureCount = failureCount;
        }
    }

}
